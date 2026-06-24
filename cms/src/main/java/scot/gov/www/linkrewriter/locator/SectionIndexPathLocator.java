package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.stripEnd;

/**
 * Resolves section-root paths to their {@code index} handle.
 *
 * <p>Handles paths such as {@code /news}, {@code /publications}, and {@code /policies}
 * that represent the landing page for a content section, as well as the special case
 * {@code /publications/statistics-and-research}.
 *
 * <p>This is needed when a redirect target is a section root (possibly with query
 * parameters — which are stripped before reaching this locator), so that the redirect
 * can still be resolved to a publishable JCR handle.
 */
public class SectionIndexPathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SectionIndexPathLocator.class);

    private static final String CONTENT_DOCUMENTS_ROOT = SlugLookup.CONTENT_DOCUMENTS_ROOT;
    private static final String INDEX = "index";

    /**
     * URL paths that map directly to a same-named folder under the content root,
     * whose {@code index} child is the landing-page handle.
     */
    private static final Set<String> SECTION_ROOTS = Set.of(
            "/news",
            "/publications",
            "/policies"
    );

    /**
     * Additional paths that resolve to a specific JCR folder (relative to the content root)
     * whose {@code index} child is the landing-page handle.
     */
    private static final java.util.Map<String, String> SPECIAL_CASES = java.util.Map.of(
            "/publications/statistics-and-research", "/publications/statistics-and-research"
    );

    @Override
    public String name() {
        return "section-index";
    }

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        String normPath = stripEnd(path.startsWith("/") ? path : "/" + path, "/");

        String jcrFolder = null;
        if (SECTION_ROOTS.contains(normPath)) {
            jcrFolder = CONTENT_DOCUMENTS_ROOT + normPath;
        } else if (SPECIAL_CASES.containsKey(normPath)) {
            jcrFolder = CONTENT_DOCUMENTS_ROOT + SPECIAL_CASES.get(normPath);
        }

        if (jcrFolder == null) {
            return Optional.empty();
        }

        String indexPath = jcrFolder + "/" + INDEX;
        if (!session.nodeExists(indexPath)) {
            LOG.info("SectionIndexPathLocator: no index node at {}", indexPath);
            return Optional.empty();
        }

        Node node = session.getNode(indexPath);
        if (!SlugLookup.isHandle(node)) {
            LOG.warn("SectionIndexPathLocator: node at {} is not a handle", indexPath);
            return Optional.empty();
        }

        return Optional.of(node);
    }
}
