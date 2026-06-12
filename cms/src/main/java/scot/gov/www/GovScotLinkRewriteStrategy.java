package scot.gov.www;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.JcrRedirectRepository;
import scot.gov.publishing.hippo.redirects.Redirect;
import scot.gov.publishing.sluglookup.SlugLookupPaths;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.stripEnd;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * Resolves legacy gov.scot links to JCR {@code hippo:handle} nodes.
 *
 * <p>Resolution is attempted in the following order for each href:
 *
 * <ol>
 *   <li><b>News by slug</b> — paths of the form {@code /news/<slug>} are resolved via the
 *       pre-built slug-lookup table under {@code /content/urls/}.  The live (published) entry
 *       is tried first; if absent the preview entry is used so that draft-only content can
 *       still be relinked.  No JCR query is issued.</li>
 *
 *   <li><b>Publications by slug</b> — paths of the form {@code /publications/<slug>} are
 *       resolved in the same way.</li>
 *
 *   <li><b>ISBN</b> — paths of the form {@code /isbn/<isbn>} are resolved by querying for a
 *       publication whose {@code govscot:isbn} property matches the normalised ISBN (lowercase,
 *       whitespace and hyphens stripped).  This mirrors the runtime redirect performed by
 *       {@code PublicationsIsbnRedirectComponent}.</li>
 *
 *   <li><b>Direct path</b> — all other paths are mapped directly onto the
 *       {@code /content/documents/govscot} hierarchy.  If the resulting JCR path is a
 *       {@code hippo:handle} it is returned.  If it is a folder containing an {@code index}
 *       handle (typical for policy-area landing pages) that index handle is returned.</li>
 *
 *   <li><b>Topic/issue page</b> — single-segment paths (e.g. {@code /programme-for-government})
 *       that were not found by the direct-path step are looked up under
 *       {@code /content/documents/govscot/topics/}.  The same handle/index-folder logic
 *       applies.</li>
 *
 *   <li><b>Redirect fallback</b> — if none of the above finds a node, the path is looked up
 *       in the redirect table via {@link JcrRedirectRepository}.  If a non-historical redirect
 *       exists, the redirect target is passed back through steps 1–3.  This covers URLs that
 *       were migrated with a redirect rather than a direct path match.</li>
 * </ol>
 *
 * <p>Accepted href forms:
 * <ul>
 *   <li>{@code https://www.gov.scot/…}</li>
 *   <li>{@code http://www.gov.scot/…}</li>
 *   <li>{@code https://www2.gov.scot/…}</li>
 *   <li>{@code http://www2.gov.scot/…}</li>
 *   <li>Site-relative paths: {@code /some/path} or {@code some/path}</li>
 * </ul>
 *
 * <p>Query strings and fragment identifiers are stripped before any lookup.
 * Hrefs that contain URL-encoding ({@code %20} etc.) or path traversal ({@code ..}) are
 * rejected before the JCR path is constructed to avoid Jackrabbit throwing on invalid paths.
 *
 * <p>If no handle can be found after all steps, the href is logged at INFO level so that
 * unresolvable links can be investigated.
 */
public class GovScotLinkRewriteStrategy implements LinkRewriteStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(GovScotLinkRewriteStrategy.class);

    static final String SITE = "govscot";

    static final String CONTENT_DOCUMENTS_ROOT = "/content/documents/govscot";

    static final String NEWS_PREFIX         = "/news/";
    static final String PUBLICATIONS_PREFIX = "/publications/";
    static final String ISBN_PREFIX         = "/isbn/";

    static final String SLUG_LOOKUP_PATH = "sluglookup:path";

    /** All gov.scot host prefixes whose path component should be resolved. */
    private static final String[] GOV_SCOT_BASES = {
            "https://www.gov.scot",
            "http://www.gov.scot",
            "https://www2.gov.scot",
            "http://www2.gov.scot",
    };

    /** Mounts tried in preference order when resolving a slug. */
    private static final String[] MOUNTS = {"live", "preview"};

    /** How a link was resolved — set on each successful call to {@link #findTargetNode}. */
    enum ResolutionType {
        NEWS, PUBLICATIONS, ISBN, PATH, TOPIC, REDIRECT
    }

    /** Set to the resolution type after each successful {@link #findTargetNode} call. */
    ResolutionType lastResolutionType;

    @Override
    public Node findTargetNode(Session session, String href, String htmlPath) throws RepositoryException {
        String path = extractPath(href);
        if (path == null) {
            return null;
        }

        // Strip query string, fragment and trailing slash before resolving
        path = substringBefore(path, "?");
        path = substringBefore(path, "#");
        path = stripEnd(path, "/");

        if (path.isEmpty()) {
            return null;
        }

        // Primary resolution: slug lookups and direct path mapping
        Node result = resolveByPath(session, path);
        if (result != null) {
            return result;
        }

        // Fallback: check the redirect table for this path
        result = findViaRedirect(session, path);
        return result;
    }

    // ---- Primary resolution ----------------------------------------------------------------

    /**
     * Attempts to resolve {@code path} via slug lookup (news/publications) or direct JCR path
     * mapping.  Returns {@code null} without logging if no node is found — callers handle the
     * not-found case.
     */
    private Node resolveByPath(Session session, String path) throws RepositoryException {
        if (path.startsWith(NEWS_PREFIX)) {
            Node result = findBySlug(session, "news", firstSegmentAfterPrefix(path, NEWS_PREFIX));
            if (result != null) lastResolutionType = ResolutionType.NEWS;
            return result;
        }
        if (path.startsWith(PUBLICATIONS_PREFIX)) {
            Node result = findBySlug(session, "publications", firstSegmentAfterPrefix(path, PUBLICATIONS_PREFIX));
            if (result != null) lastResolutionType = ResolutionType.PUBLICATIONS;
            return result;
        }
        if (path.startsWith(ISBN_PREFIX) || path.startsWith("/ISBN/")) {
            Node result = findByIsbn(session, firstSegmentAfterPrefix(path, ISBN_PREFIX));
            if (result != null) lastResolutionType = ResolutionType.ISBN;
            return result;
        }
        Node result = findByJcrPath(session, path);
        if (result != null) {
            lastResolutionType = ResolutionType.PATH;
            return result;
        }
        // Single-segment paths (e.g. /programme-for-government) may be topic/issue pages
        // stored under /content/documents/govscot/topics/
        if (isSingleSegmentPath(path)) {
            result = findByTopicPath(session, path);
            if (result != null) lastResolutionType = ResolutionType.TOPIC;
            return result;
        }
        return null;
    }

    // ---- Slug-based resolution via lookup table --------------------------------------------

    /**
     * Looks up the {@code hippo:handle} for the given {@code slug} under {@code contentArea}
     * using the pre-built slug-lookup table under {@code /content/urls/}.
     *
     * <p>The {@code live} mount is tried first; if absent {@code preview} is used so that
     * draft-only documents can still be relinked.  No JCR query is issued.
     */
    Node findBySlug(Session session, String contentArea, String slug) throws RepositoryException {
        if (slug == null || slug.isEmpty()) {
            return null;
        }

        for (String mount : MOUNTS) {
            String lookupPath = SlugLookupPaths.slugLookupPath(slug, SITE, contentArea, mount);
            if (!session.nodeExists(lookupPath)) {
                continue;
            }
            Node lookupNode = session.getNode(lookupPath);
            if (!lookupNode.hasProperty(SLUG_LOOKUP_PATH)) {
                continue;
            }

            String contentPath = lookupNode.getProperty(SLUG_LOOKUP_PATH).getString();
            String jcrPath = CONTENT_DOCUMENTS_ROOT + contentPath;

            if (!session.nodeExists(jcrPath)) {
                LOG.debug("GovScotLinkRewriteStrategy: lookup entry for slug '{}' points to missing node {}",
                        slug, jcrPath);
                continue;
            }

            Node handle = session.getNode(jcrPath);
            if (handle.isNodeType("hippo:handle")) {
                return handle;
            }
        }

        return null;
    }

    // ---- ISBN-based resolution -------------------------------------------------------------

    /**
     * Finds the {@code hippo:handle} for the publication whose {@code govscot:isbn} property
     * matches {@code rawIsbn} after normalisation (lowercase, whitespace and hyphens stripped).
     *
     * <p>The lookup is a single-property XPath query scoped to the govscot content tree and
     * excluding frozen (version-history) nodes.  If multiple matches exist the first is used
     * and a warning is logged.
     */
    Node findByIsbn(Session session, String rawIsbn) throws RepositoryException {
        if (rawIsbn == null || rawIsbn.isEmpty()) {
            return null;
        }
        String isbn = rawIsbn.toLowerCase().replaceAll("[\\s\\-]", "");

        String xpath = "/jcr:root" + CONTENT_DOCUMENTS_ROOT
                + "//element(*)[not(@jcr:primaryType='nt:frozenNode')"
                + " and @govscot:isbn='" + isbn + "']";

        NodeIterator nodes = session.getWorkspace().getQueryManager()
                .createQuery(xpath, Query.XPATH)
                .execute()
                .getNodes();

        if (!nodes.hasNext()) {
            LOG.debug("GovScotLinkRewriteStrategy: no publication found for ISBN '{}'", rawIsbn);
            return null;
        }

        Node variant = nodes.nextNode();

        if (nodes.hasNext()) {
            LOG.warn("GovScotLinkRewriteStrategy: multiple publications found for ISBN '{}', using first", rawIsbn);
        }

        Node handle = variant.getParent();
        if (handle.isNodeType("hippo:handle")) {
            return handle;
        }

        LOG.warn("GovScotLinkRewriteStrategy: parent of ISBN '{}' variant is not a handle ({})",
                rawIsbn, handle.getPrimaryNodeType().getName());
        return null;
    }

    // ---- Direct JCR path resolution --------------------------------------------------------

    private Node findByJcrPath(Session session, String path) throws RepositoryException {
        // Reject paths that cannot be valid JCR paths and would cause Jackrabbit to throw:
        //   - URL-encoded characters (%20 etc.) are illegal in JCR node names
        //   - '..' segments are relative traversal and rejected by the JCR path parser
        if (path.contains("%") || path.contains("..")) {
            LOG.debug("GovScotLinkRewriteStrategy: path '{}' contains URL-encoding or traversal, skipping", path);
            return null;
        }

        String jcrPath = CONTENT_DOCUMENTS_ROOT + (path.startsWith("/") ? path : "/" + path);

        if (!session.nodeExists(jcrPath)) {
            return null;
        }

        Node node = session.getNode(jcrPath);

        if (node.isNodeType("hippo:handle")) {
            return node;
        }

        // Folder with a conventional index handle (e.g. policy-area landing page)
        if (node.isNodeType("hippostd:folder") && node.hasNode("index")) {
            return node.getNode("index");
        }

        LOG.debug("GovScotLinkRewriteStrategy: node at {} is neither a handle nor an indexable folder", jcrPath);
        return null;
    }

    // ---- Topic/issue page resolution -------------------------------------------------------

    /**
     * Returns {@code true} when {@code path} has exactly one non-empty segment — e.g.
     * {@code /programme-for-government} or {@code programme-for-government}.
     */
    private static boolean isSingleSegmentPath(String path) {
        String segment = path.startsWith("/") ? path.substring(1) : path;
        return !segment.isEmpty() && !segment.contains("/");
    }

    /**
     * Tries to resolve a single-segment path as a topic or issue page under
     * {@code /content/documents/govscot/topics/}.  Applies the same handle/index-folder
     * logic as {@link #findByJcrPath}.
     */
    private Node findByTopicPath(Session session, String path) throws RepositoryException {
        String segment = path.startsWith("/") ? path.substring(1) : path;
        String topicPath = CONTENT_DOCUMENTS_ROOT + "/topics/" + segment;

        if (!session.nodeExists(topicPath)) {
            return null;
        }

        Node node = session.getNode(topicPath);

        if (node.isNodeType("hippo:handle")) {
            return node;
        }

        if (node.isNodeType("hippostd:folder") && node.hasNode("index")) {
            return node.getNode("index");
        }

        LOG.debug("GovScotLinkRewriteStrategy: topic node at {} is neither a handle nor an indexable folder", topicPath);
        return null;
    }

    // ---- Redirect fallback -----------------------------------------------------------------

    /**
     * Looks up {@code path} in the redirect table via {@link JcrRedirectRepository}.  If a
     * non-historical redirect is found its target is passed back through {@link #resolveByPath}
     * (not recursively through this method, so redirect chains are not followed).  Returns
     * {@code null} if no usable redirect exists.
     *
     * <p>{@code path} must already have its trailing slash stripped before this method is
     * called (ensured by {@link #findTargetNode}).
     */
    private Node findViaRedirect(Session session, String path) throws RepositoryException {
        JcrRedirectRepository redirects = new JcrRedirectRepository(session);
        Optional<Redirect> redirect = redirects.lookup(path);

        if (redirect.isEmpty()) {
            return null;
        }

        if (redirect.get().isHistoricalUrl()) {
            return null;
        }

        String redirectTo = redirect.get().getTo();

        // Strip host and normalise the redirect target, then try a single level of resolution
        String redirectPath = extractPath(redirectTo);
        if (redirectPath == null) {
            return null;
        }
        redirectPath = stripEnd(substringBefore(substringBefore(redirectPath, "?"), "#"), "/");
        Node resolved = resolveByPath(session, redirectPath);
        if (resolved != null) {
            lastResolutionType = ResolutionType.REDIRECT;
        }
        return resolved;
    }

    // ---- Helpers ---------------------------------------------------------------------------

    /**
     * Strips a known gov.scot host prefix from {@code href} and returns the path component,
     * or returns {@code null} for hrefs that should not be resolved (other hosts, mailto, tel,
     * bare fragments, etc.).
     */
    String extractPath(String href) {
        if (href == null || href.isEmpty()) {
            return null;
        }
        for (String base : GOV_SCOT_BASES) {
            if (href.startsWith(base)) {
                return href.substring(base.length());
            }
        }
        // Any other absolute URL, mailto, tel, etc. — leave alone
        if (href.contains("://") || href.startsWith("mailto:") || href.startsWith("tel:")) {
            return null;
        }
        // Bare fragment — nothing to resolve
        if (href.startsWith("#")) {
            return null;
        }
        return href;
    }

    /**
     * Extracts the first path segment following {@code prefix}.
     * For example, {@code "/news/some-story/details"} with prefix {@code "/news/"} returns
     * {@code "some-story"}.
     */
    String firstSegmentAfterPrefix(String path, String prefix) {
        String remainder = path.substring(prefix.length());
        int slash = remainder.indexOf('/');
        return slash < 0 ? remainder : remainder.substring(0, slash);
    }
}
