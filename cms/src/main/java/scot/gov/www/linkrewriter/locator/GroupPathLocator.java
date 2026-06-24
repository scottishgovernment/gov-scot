package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;

/**
 * Resolves {@code /groups/<slug>[/<sub-page>]} paths by direct JCR path mapping.
 *
 * <p>Group content lives under {@code /content/documents/govscot/groups/}.  The URL path
 * maps directly onto the JCR tree: a folder represents a group, and the {@code index}
 * child handle represents the top-level group page.  Sub-pages are sibling handles within
 * the same folder.
 *
 * <p>Examples:
 * <ul>
 *   <li>{@code /groups/scottish-government/} → {@code .../groups/scottish-government/index}</li>
 *   <li>{@code /groups/scottish-government/about/} → {@code .../groups/scottish-government/about/index}</li>
 * </ul>
 */
public class GroupPathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(GroupPathLocator.class);

    static final String GROUPS_PREFIX = "/groups/";

    private static final String CONTENT_DOCUMENTS_ROOT = SlugLookup.CONTENT_DOCUMENTS_ROOT;
    private static final String INDEX = "index";

    @Override
    public String name() {
        return "group";
    }

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        if (!path.startsWith(GROUPS_PREFIX)) {
            return Optional.empty();
        }
        if (path.contains("%") || path.contains("..")) {
            LOG.warn("GroupPathLocator: path '{}' contains URL-encoding or traversal, skipping", path);
            return Optional.empty();
        }
        if (DirectPathLocator.containsWhitespace(path)) {
            LOG.warn("GroupPathLocator: path '{}' contains whitespace, skipping", path);
            return Optional.empty();
        }
        if (DirectPathLocator.hasInvalidJcrSegment(path)) {
            LOG.debug("GroupPathLocator: path '{}' contains an invalid JCR character, skipping", path);
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
        LOG.warn("GroupPathLocator: node at {} is neither a handle nor an indexable folder", jcrPath);
        return Optional.empty();
    }


}
