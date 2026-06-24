package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;

/**
 * Resolves URLs by mapping them directly onto the {@code /content/documents/govscot} JCR
 * hierarchy.
 *
 * <p>Resolution order:
 * <ol>
 *   <li>Direct JCR path — the URL path is appended to the content root.  Returns a handle
 *       or the {@code index} child of a folder.  (Documents sub-folder paths are handled by
 *       {@link PublicationDocumentsPathLocator}, which runs before this locator.)</li>
 *   <li>Topics — single-segment paths are tried under {@code .../govscot/topics/}.</li>
 *   <li>Site items — single-segment paths are tried under {@code .../govscot/siteitems/}.</li>
 * </ol>
 */
public class DirectPathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DirectPathLocator.class);

    private static final String CONTENT_DOCUMENTS_ROOT = SlugLookup.CONTENT_DOCUMENTS_ROOT;
    private static final String INDEX                  = "index";

    @Override
    public String name() {
        return "path";
    }

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        if (hasInvalidJcrSegment(path)) {
            LOG.debug("DirectPathLocator: path '{}' contains an invalid JCR character — not a valid document path", path);
            return Optional.empty();
        }
        Optional<Node> byPath = findByJcrPath(session, path);
        if (byPath.isPresent()) {
            return byPath;
        }
        if (isSingleSegmentPath(path)) {
            Optional<Node> byTopic = findUnderContentSubfolder(session, path, "topics");
            if (byTopic.isPresent()) {
                return byTopic;
            }
            return findUnderContentSubfolder(session, path, "siteitems");
        }
        return Optional.empty();
    }

    private Optional<Node> findByJcrPath(Session session, String path) throws RepositoryException {
        if (path.contains("%") || path.contains("..")) {
            LOG.warn("DirectPathLocator: path '{}' contains URL-encoding or traversal, skipping", path);
            return Optional.empty();
        }
        if (containsWhitespace(path)) {
            LOG.warn("DirectPathLocator: path '{}' contains whitespace, skipping", path);
            return Optional.empty();
        }
        String jcrPath = CONTENT_DOCUMENTS_ROOT + (path.startsWith("/") ? path : "/" + path);
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
        LOG.warn("DirectPathLocator: node at {} is neither a handle nor an indexable folder", jcrPath);
        return Optional.empty();
    }

    private Optional<Node> findUnderContentSubfolder(Session session, String path,
                                                      String subfolder) throws RepositoryException {
        String segment  = path.startsWith("/") ? path.substring(1) : path;
        String nodePath = CONTENT_DOCUMENTS_ROOT + "/" + subfolder + "/" + segment;
        if (!session.nodeExists(nodePath)) {
            return Optional.empty();
        }
        Node node = session.getNode(nodePath);
        if (SlugLookup.isHandle(node)) {
            return Optional.of(node);
        }
        if (node.isNodeType("hippostd:folder") && node.hasNode(INDEX)) {
            return Optional.of(node.getNode(INDEX));
        }
        LOG.warn("DirectPathLocator: node at {} ({}) is neither a handle nor an indexable folder",
                nodePath, subfolder);
        return Optional.empty();
    }

    private static boolean isSingleSegmentPath(String path) {
        String segment = path.startsWith("/") ? path.substring(1) : path;
        return !segment.isEmpty() && !segment.contains("/");
    }

    static boolean containsWhitespace(String path) {
        for (int i = 0; i < path.length(); i++) {
            if (Character.isWhitespace(path.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if any segment of {@code path} contains a character that is
     * invalid in a JCR path segment, indicating the href is not a resolvable document URL.
     *
     * <p>Invalid characters:
     * <ul>
     *   <li>{@code :} — reserved as the namespace prefix separator (e.g. {@code jcr:content});
     *       document content paths never carry namespace prefixes, so any colon signals a
     *       malformed href (e.g. {@code /Publications/2015/11/2023:05:00})</li>
     *   <li>{@code [} {@code ]} — used by JCR for same-name sibling indices
     *       (e.g. {@code node[2]})</li>
     *   <li>{@code (} {@code )} — not a legal JCR name character; indicates malformed content
     *       such as Markdown link syntax that was not converted to HTML
     *       (e.g. {@code /foi-responses](http://www.gov.scot/foi-responses})</li>
     *   <li>{@code *} — JCR wildcard, illegal in absolute paths</li>
     *   <li>{@code |} — XPath union operator, illegal in absolute paths</li>
     * </ul>
     */
    static boolean hasInvalidJcrSegment(String path) {
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == ':' || c == '[' || c == ']' || c == '(' || c == ')' || c == '*' || c == '|') {
                return true;
            }
        }
        return false;
    }
}
