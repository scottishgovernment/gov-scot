package scot.gov.www;

import org.onehippo.cms7.services.eventbus.HippoEventListenerRegistry;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.equalsAny;

public class SitemapEventListener extends AbstractReconfigurableDaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapEventListener.class);

    public static final String SITEMAP_ROOT = "/content/sitemaps/";

    private static final String PUBLISH_INTERACTION = "default:handle:publish";

    private static final String DEPUBLISH_INTERACTION = "default:handle:depublish";

    private static final String LAST_MOD = "govscot:lastMod";

    private static final String NT_UNSTRUCTURED = "nt:unstructured";

    private static final String EXCLUDED_TYPES = "govscot:excludedTypes";

    private HippoUtils hippoUtils = new HippoUtils();

    private UrlSource urlSource = new UrlSource();

    private Set<String> exludedTypes = new HashSet<>();

    @Override
    protected void doConfigure(Node node) throws RepositoryException {
        if (node.hasProperty(EXCLUDED_TYPES)) {
            Value[] excludedTypesValues = node.getProperty(EXCLUDED_TYPES).getValues();
            for (Value value : excludedTypesValues) {
                exludedTypes.add(value.getString());
            }
        }
    }

    @Override
    protected void doInitialize(Session session) throws RepositoryException {
        HippoEventListenerRegistry.get().register(this);
    }

    @Override
    protected void doShutdown() {
        HippoEventListenerRegistry.get().unregister(this);
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {

        if (!shouldHandleEvent(event)) {
            return;
        }

        try {
            Node handle = session.getNodeByIdentifier(event.subjectId());
            Node node = hippoUtils.getVariant(handle);

            if (isExcludedType(node)) {
                return;
            }

            String url = urlSource.url(node);
            removeExistingSitemapNode(url, node);
            if (PUBLISH_INTERACTION.equals(event.interaction())) {
                ensureSitemapEntry(node, url);
            }
            session.save();
        } catch (RepositoryException e) {
            LOG.error("RepositoryException trying to index {}", event.subjectId(), e);
        }
    }

    boolean isExcludedType(Node node) throws RepositoryException {
        for (String type : exludedTypes) {
            if (node.isNodeType(type)) {
                return true;
            }
        }
        return false;
    }

    void removeExistingSitemapNode(String url, Node node) throws RepositoryException {
        String sitename = UrlSource.sitename(node);
        String xpath = "/jcr:root/content/sitemaps/%s//uuid-%s";
        String handleIdentifier = node.getParent().getIdentifier();
        Node currentNode = hippoUtils.findOneQuery(session, xpath, Query.XPATH, sitename, handleIdentifier);
        if (currentNode != null) {
            LOG.info("removing sitemap node for {}, {}", url, node.getPath());
            // the sitemap that this url belongs to has changed
            currentNode.getParent().setProperty(LAST_MOD, Calendar.getInstance());

            // remove the old entry
            currentNode.remove();
        }
    }

    void ensureSitemapEntry(Node node, String url) throws RepositoryException {
        Calendar lastModified = node.getProperty("hippostdpubwf:lastModificationDate").getDate();
        Node sitemapNode = getSitemapSiteNode(node);
        Node yearNode = yearNode(sitemapNode, lastModified);
        String month = Integer.toString(lastModified.get(Calendar.MONTH) + 1);
        boolean hasMonthSitemap = yearNode.hasNode(month);
        Node monthNode =  hasMonthSitemap ? yearNode.getNode(month) : yearNode.addNode(month, NT_UNSTRUCTURED);
        monthNode.setProperty(LAST_MOD, Calendar.getInstance());

        Node urlNode = monthNode.addNode("uuid-" + node.getParent().getIdentifier(), NT_UNSTRUCTURED);
        LOG.info("update sitemap node for {}, {}", url, urlNode.getPath());
        urlNode.setProperty("govscot:loc", url);
        urlNode.setProperty(LAST_MOD, lastModified);
        if (!hasMonthSitemap) {
            sitemapNode.setProperty(LAST_MOD, Calendar.getInstance());
        }
    }

    Node getSitemapSiteNode(Node node) throws RepositoryException {
        Node sitemapRoot = session.getNode(SITEMAP_ROOT);
        String sitename = UrlSource.sitename(node);
        return sitemapRoot.getNode(sitename);
    }

    Node yearNode(Node sitemapNode, Calendar lastModified) throws RepositoryException {
        String year = Integer.toString(lastModified.get(Calendar.YEAR));
        return sitemapNode.hasNode(year)
                ? sitemapNode.getNode(year)
                : sitemapNode.addNode(year, NT_UNSTRUCTURED);
    }

    /**
     * we are only interested in successful publish and depublish events for news and publications
     */
    boolean shouldHandleEvent(HippoWorkflowEvent event) {
        if (!event.success()) {
            return false;
        }

        return equalsAny(event.interaction(), PUBLISH_INTERACTION, DEPUBLISH_INTERACTION);
    }
}