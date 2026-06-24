package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.sluglookup.SlugLookupPaths;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;

/**
 * Shared slug-lookup logic used by {@link NewsPathLocator} and {@link PublicationsPathLocator}.
 *
 * <p>Slugs are resolved via the pre-built lookup table under {@code /content/urls/}
 * using the {@code live} mount only — unpublished documents are never link targets.
 * No JCR query is issued.
 */
public class SlugLookup {

    private static final Logger LOG = LoggerFactory.getLogger(SlugLookup.class);

    static final String SITE = "govscot";
    static final String CONTENT_DOCUMENTS_ROOT = "/content/documents/govscot";
    static final String SLUG_LOOKUP_PATH_PROP = "sluglookup:path";

    private static final String[] MOUNTS = {"live"};

    private SlugLookup() {}

    /**
     * Looks up the {@code hippo:handle} for {@code slug} within {@code contentArea}
     * (e.g. {@code "news"} or {@code "publications"}).
     */
    static Optional<Node> findBySlug(Session session, String contentArea, String slug)
            throws RepositoryException {
        if (slug == null || slug.isEmpty()) {
            return Optional.empty();
        }
        for (String mount : MOUNTS) {
            Optional<Node> handle = findBySlugMount(session, contentArea, slug, mount);
            if (handle.isPresent()) {
                return handle;
            }
        }
        return Optional.empty();
    }

    private static Optional<Node> findBySlugMount(Session session, String contentArea,
                                                   String slug, String mount)
            throws RepositoryException {
        String lookupPath = SlugLookupPaths.slugLookupPath(slug, SITE, contentArea, mount);
        if (!session.nodeExists(lookupPath)) {
            return Optional.empty();
        }
        Node lookupNode = session.getNode(lookupPath);
        if (!lookupNode.hasProperty(SLUG_LOOKUP_PATH_PROP)) {
            return Optional.empty();
        }
        String jcrPath = CONTENT_DOCUMENTS_ROOT + lookupNode.getProperty(SLUG_LOOKUP_PATH_PROP).getString();
        if (!session.nodeExists(jcrPath)) {
            LOG.debug("SlugLookup: lookup entry for slug '{}' points to missing node {}", slug, jcrPath);
            return Optional.empty();
        }
        Node handle = session.getNode(jcrPath);
        return isHandle(handle) ? Optional.of(handle) : Optional.empty();
    }

    static boolean isHandle(Node node) throws RepositoryException {
        return node.isNodeType("hippo:handle");
    }
}
