package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;

/**
 * Resolves {@code /policies/<slug>[/<sub-page>]} paths by direct JCR path mapping.
 *
 * <p>Policy content lives under {@code /content/documents/govscot/policies/}.  The URL path
 * maps directly onto the JCR tree: a folder represents a policy area, and the
 * {@code index} child handle represents the top-level policy page.  Sub-pages are sibling
 * handles within the same folder.
 *
 * <p>Examples:
 * <ul>
 *   <li>{@code /policies/international-development/} → {@code .../policies/international-development/index}</li>
 *   <li>{@code /policies/international-development/responding-to-humanitarian-crises/}
 *       → {@code .../policies/international-development/responding-to-humanitarian-crises/index}</li>
 * </ul>
 */
public class PolicyPathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(PolicyPathLocator.class);

    static final String POLICIES_PREFIX = "/policies/";

    private static final String CONTENT_DOCUMENTS_ROOT = SlugLookup.CONTENT_DOCUMENTS_ROOT;
    private static final String INDEX = "index";

    @Override
    public String name() {
        return "policy";
    }

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        if (!path.startsWith(POLICIES_PREFIX)) {
            return Optional.empty();
        }
        if (path.contains("%") || path.contains("..")) {
            LOG.warn("PolicyPathLocator: path '{}' contains URL-encoding or traversal, skipping", path);
            return Optional.empty();
        }
        if (DirectPathLocator.containsWhitespace(path)) {
            LOG.warn("PolicyPathLocator: path '{}' contains whitespace, skipping", path);
            return Optional.empty();
        }
        if (DirectPathLocator.hasInvalidJcrSegment(path)) {
            LOG.debug("PolicyPathLocator: path '{}' contains an invalid JCR character, skipping", path);
            return Optional.empty();
        }

        String jcrPath = CONTENT_DOCUMENTS_ROOT + path;
        if (!session.nodeExists(jcrPath)) {
            return Optional.empty();
        }
        Node node = session.getNode(jcrPath);
        if (SlugLookup.isHandle(node)) {
            return Optional.of(node);
        }
        if (node.isNodeType("hippostd:folder") && node.hasNode(INDEX)) {
            return Optional.of(node.getNode(INDEX));
        }
        LOG.warn("PolicyPathLocator: node at {} is neither a handle nor an indexable folder", jcrPath);
        return Optional.empty();
    }


}
