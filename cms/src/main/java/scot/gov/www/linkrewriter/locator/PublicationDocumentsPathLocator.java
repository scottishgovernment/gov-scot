package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;

/**
 * Resolves paths that address the {@code documents} sub-folder of a publication.
 *
 * <ul>
 *   <li>{@code …/documents} — returns the first document handle in the folder.</li>
 *   <li>{@code …/documents/<name>} — returns the handle named {@code <name>} if one exists
 *       in the folder, otherwise falls back to the first document handle.</li>
 * </ul>
 *
 * <p>Only a single additional path segment after {@code /documents} is supported; paths
 * with deeper sub-paths are ignored so other locators can attempt resolution.
 */
public class PublicationDocumentsPathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationDocumentsPathLocator.class);

    private static final String CONTENT_DOCUMENTS_ROOT = SlugLookup.CONTENT_DOCUMENTS_ROOT;
    private static final String DOCUMENTS = "documents";

    @Override
    public String name() {
        return "publication-documents";
    }

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        String normPath = path.startsWith("/") ? path : "/" + path;

        int docsStart = indexOfDocumentsSegment(normPath);
        if (docsStart < 0) {
            return Optional.empty();
        }

        String beforeDocs = normPath.substring(0, docsStart);
        String afterDocs  = normPath.substring(docsStart + "/documents".length());

        // afterDocs is either "" (path ends at /documents) or "/<filename>"
        String filename = afterDocs.isEmpty() ? "" : afterDocs.substring(1);

        if (filename.contains("/")) {
            // Deeper sub-path — not handled here
            return Optional.empty();
        }

        String docsFolderPath = CONTENT_DOCUMENTS_ROOT + beforeDocs + "/documents";
        if (!session.nodeExists(docsFolderPath)) {
            return Optional.empty();
        }

        Node docsFolder = session.getNode(docsFolderPath);
        if (!docsFolder.isNodeType("hippostd:folder") || !DOCUMENTS.equals(docsFolder.getName())) {
            return Optional.empty();
        }

        if (!filename.isEmpty()) {
            if (docsFolder.hasNode(filename)) {
                Node candidate = docsFolder.getNode(filename);
                if (SlugLookup.isHandle(candidate)) {
                    return Optional.of(candidate);
                }
            }
            LOG.info("PublicationDocumentsPathLocator: '{}' — no handle named '{}' in {}, falling back to first document",
                    path, filename, docsFolderPath);
        }

        return firstHandle(docsFolder, docsFolderPath, path);
    }

    /**
     * Returns the index of {@code /documents} as a complete path segment within {@code path},
     * or {@code -1} if not present.
     */
    private static int indexOfDocumentsSegment(String path) {
        int idx = path.indexOf("/documents/");
        if (idx >= 0) {
            return idx;
        }
        if (path.endsWith("/documents")) {
            return path.length() - "/documents".length();
        }
        return -1;
    }

    private static Optional<Node> firstHandle(Node docsFolder, String docsFolderPath,
                                               String path) throws RepositoryException {
        NodeIterator it = docsFolder.getNodes();
        while (it.hasNext()) {
            Node child = it.nextNode();
            if (SlugLookup.isHandle(child)) {
                return Optional.of(child);
            }
        }
        LOG.warn("PublicationDocumentsPathLocator: documents folder at {} contains no handles (path='{}')",
                docsFolderPath, path);
        return Optional.empty();
    }
}
