package scot.gov.www.importer.sink;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.sluglookup.SlugLookupPaths;
import scot.gov.publishing.sluglookup.SlugLookups;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;

/**
 * Performs, for news items only, the equivalent of what NewsSlugDaemonModule, SitemapEventListener
 * and SlugMaintenanceListener would otherwise do asynchronously on their own JCR session when the
 * importer publishes/depublishes a news item.
 *
 * Those listeners are gated to skip events triggered by the importer under the news path (see
 * their canHandleEvent/shouldHandleEvent methods) because their callback runs on a session that is
 * entirely separate from the importer's session (each DaemonModule gets its own session once, at
 * startup) - so nothing the importer does can ever cause a listener's pending changes to be
 * persisted. Doing the equivalent work here instead, on the importer's own session, means it is
 * persisted by saves the importer already performs, and avoids each item triggering three
 * additional, separately-dispatched listener saves against the same handle.
 */
public class NewsSideEffects {

    private static final Logger LOG = LoggerFactory.getLogger(NewsSideEffects.class);

    private static final String GOVSCOT_SLUG_PROPERTY = "govscot:slug";

    private static final String DOC_TYPE = "news";

    private static final String LIVE = "live";

    private static final String SITEMAP_ROOT = "/content/sitemaps/";

    private static final String LAST_MOD = "govscot:lastMod";

    private static final String NT_UNSTRUCTURED = "nt:unstructured";

    private static final String SITEMAP_MODULE_CONFIG = "/hippo:configuration/hippo:modules/sitemap/hippo:moduleconfig";

    private static final String EXCLUDED_TYPES_PROPERTY = "govscot:excludedTypes";

    private static final String NEWS_NODE_TYPE = "govscot:News";

    /**
     * Resolves the govscot:slug value to use for this item, WITHOUT writing to any JCR node.
     * Must be called before the ContentNode is bound - content-exim's binder can only write to a
     * document variant while it is checked out via obtainEditableDocument()/before
     * commitEditableDocument() (that privileged window is how title/content/tags/etc. all get
     * set successfully); trying to set a property directly on a variant from outside that window
     * - i.e. after createOrUpdate()/publish() have already run - is denied
     * (AccessDeniedException), regardless of the variant's published/unpublished state. So the
     * slug has to be decided up front and passed into the ContentNode to be bound the same way
     * every other property is, rather than set afterwards.
     */
    public String resolveSlug(Session session, String location, String seoName) throws RepositoryException {
        if (session.nodeExists(location)) {
            Node handle = session.getNode(location);
            for (Node variant : allVariants(handle)) {
                // the govscot:slug field can be present but empty (e.g. bound as an empty
                // string by the content type's default), not just absent
                if (variant.hasProperty(GOVSCOT_SLUG_PROPERTY)
                        && !isBlank(variant.getProperty(GOVSCOT_SLUG_PROPERTY).getString())) {
                    String existingSlug = variant.getProperty(GOVSCOT_SLUG_PROPERTY).getString();
                    LOG.warn("resolveSlug: reusing existing slug '{}' for {}", existingSlug, location);
                    return existingSlug;
                }
            }
        }

        String name = !isBlank(seoName) ? seoName : StringUtils.substringAfterLast(location, "/");
        String slug = allocate(session, name);
        LOG.warn("resolveSlug: allocated new slug '{}' (source name '{}') for {}", slug, name, location);
        return slug;
    }

    /**
     * Must be called AFTER publish(), once the published variant exists (with the slug already
     * carried in via the ContentNode binding). Only reads variant state - never writes to the
     * published variant.
     */
    public void afterPublish(Session session, Node handle) throws RepositoryException {
        Node variant = latestVariant(handle);
        if (variant == null) {
            LOG.warn("afterPublish: no variant found under handle {}", handle.getPath());
            return;
        }

        LOG.warn("afterPublish: slug for {} is '{}'", handle.getPath(),
                variant.hasProperty(GOVSCOT_SLUG_PROPERTY) ? variant.getProperty(GOVSCOT_SLUG_PROPERTY).getString() : null);

        try {
            updateSitemapEntry(session, handle, variant);
        } catch (RepositoryException | RuntimeException e) {
            LOG.error("Failed to update sitemap entry for {}", handle.getPath(), e);
            session.refresh(false);
        }

        try {
            updateSlugLookup(session, handle);
        } catch (RepositoryException | RuntimeException e) {
            LOG.error("Failed to update slug lookup for {}", handle.getPath(), e);
            session.refresh(false);
        }
    }

    public void onDepublish(Session session, Node handle) throws RepositoryException {
        try {
            removeExistingSitemapEntry(session, handle);
        } catch (RepositoryException | RuntimeException e) {
            LOG.error("Failed to remove sitemap entry for {}", handle.getPath(), e);
            session.refresh(false);
        }

        try {
            removeSlugLookup(session, handle);
        } catch (RepositoryException | RuntimeException e) {
            LOG.error("Failed to remove slug lookup for {}", handle.getPath(), e);
            session.refresh(false);
        }
    }

    private String allocate(Session session, String slug) throws RepositoryException {
        slug = removeDuplicateHyphens(slug);
        if (!slugAlreadyExists(session, slug)) {
            return slug;
        }
        return disambiguate(session, slug, 2);
    }

    private String disambiguate(Session session, String slug, int postfix) throws RepositoryException {
        String candidate = String.format("%s-%d", slug, postfix);
        if (!slugAlreadyExists(session, candidate)) {
            return candidate;
        }
        return disambiguate(session, slug, postfix + 1);
    }

    private boolean slugAlreadyExists(Session session, String slug) throws RepositoryException {
        String slugPath = SlugLookupPaths.slugLookupPath(slug, "govscot", DOC_TYPE, LIVE);
        return session.nodeExists(slugPath);
    }

    private String removeDuplicateHyphens(String str) {
        return Arrays.stream(str.split("-"))
                .filter(StringUtils::isNotEmpty)
                .collect(joining("-"));
    }

    private void updateSitemapEntry(Session session, Node handle, Node variant) throws RepositoryException {
        if (isNewsTypeExcludedFromSitemap(session)) {
            return;
        }

        Node sitemapNode = getSitemapSiteNode(session, handle);
        if (sitemapNode == null) {
            // sitemap structure not present (e.g. local/dev environment) - nothing to do
            return;
        }

        removeExistingSitemapEntry(session, handle);

        if (!variant.hasProperty(GOVSCOT_SLUG_PROPERTY)) {
            return;
        }

        Calendar lastModified = variant.hasProperty("hippostdpubwf:lastModificationDate")
                ? variant.getProperty("hippostdpubwf:lastModificationDate").getDate()
                : Calendar.getInstance();
        Node monthNode = getMonthNode(session, handle, lastModified);
        if (monthNode == null) {
            return;
        }

        String url = "/news/" + variant.getProperty(GOVSCOT_SLUG_PROPERTY).getString() + "/";
        Node urlNode = monthNode.addNode("uuid-" + handle.getIdentifier(), NT_UNSTRUCTURED);
        urlNode.setProperty("govscot:loc", url);
        urlNode.setProperty(LAST_MOD, lastModified);
    }

    private void removeExistingSitemapEntry(Session session, Node handle) throws RepositoryException {
        if (isNewsTypeExcludedFromSitemap(session)) {
            return;
        }

        if (getSitemapSiteNode(session, handle) == null) {
            return;
        }

        String sitename = sitename(handle);
        String xpath = String.format("/jcr:root/content/sitemaps/%s//uuid-%s", sitename, handle.getIdentifier());
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        QueryResult result = query.execute();
        NodeIterator it = result.getNodes();
        while (it.hasNext()) {
            it.nextNode().remove();
        }
    }

    private Node getMonthNode(Session session, Node handle, Calendar lastModified) throws RepositoryException {
        Node sitemapNode = getSitemapSiteNode(session, handle);
        if (sitemapNode == null) {
            return null;
        }

        String year = Integer.toString(lastModified.get(Calendar.YEAR));
        String month = Integer.toString(lastModified.get(Calendar.MONTH) + 1);

        if (!sitemapNode.hasNode(year)) {
            return null;
        }
        Node yearNode = sitemapNode.getNode(year);

        if (!yearNode.hasNode(month)) {
            return null;
        }
        return yearNode.getNode(month);
    }

    private Node getSitemapSiteNode(Session session, Node handle) throws RepositoryException {
        if (!session.nodeExists(SITEMAP_ROOT)) {
            return null;
        }

        Node sitemapRoot = session.getNode(SITEMAP_ROOT);
        String sitename = sitename(handle);
        if (!sitemapRoot.hasNode(sitename)) {
            return null;
        }
        return sitemapRoot.getNode(sitename);
    }

    private boolean isNewsTypeExcludedFromSitemap(Session session) throws RepositoryException {
        if (!session.nodeExists(SITEMAP_MODULE_CONFIG)) {
            return false;
        }

        Node config = session.getNode(SITEMAP_MODULE_CONFIG);
        if (!config.hasProperty(EXCLUDED_TYPES_PROPERTY)) {
            return false;
        }

        for (Value value : config.getProperty(EXCLUDED_TYPES_PROPERTY).getValues()) {
            if (NEWS_NODE_TYPE.equals(value.getString())) {
                return true;
            }
        }
        return false;
    }

    private void updateSlugLookup(Session session, Node handle) throws RepositoryException {
        Node variant = latestVariant(handle);
        if (variant == null || !variant.hasProperty(GOVSCOT_SLUG_PROPERTY)) {
            return;
        }

        String slug = variant.getProperty(GOVSCOT_SLUG_PROPERTY).getString();
        if (isBlank(slug)) {
            return;
        }

        String site = sitename(handle);
        String path = substringAfter(handle.getPath(), site);
        String type = path.split("/")[1];

        new SlugLookups(session).updateLookup(slug, path, site, type, LIVE, true);
    }

    private void removeSlugLookup(Session session, Node handle) throws RepositoryException {
        String site = sitename(handle);
        String path = substringAfter(handle.getPath(), site);
        String type = path.split("/")[1];

        new SlugLookups(session).removeLookup(path, site, type, LIVE);
    }

    private String sitename(Node node) throws RepositoryException {
        return node.getPath().split("/")[3];
    }

    private Node latestVariant(Node handle) throws RepositoryException {
        NodeIterator it = handle.getNodes();
        Node variant = null;
        while (it.hasNext()) {
            variant = it.nextNode();
        }
        return variant;
    }

    private List<Node> allVariants(Node handle) throws RepositoryException {
        List<Node> variants = new ArrayList<>();
        NodeIterator it = handle.getNodes();
        while (it.hasNext()) {
            variants.add(it.nextNode());
        }
        return variants;
    }

}
