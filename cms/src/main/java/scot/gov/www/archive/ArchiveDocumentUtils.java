package scot.gov.www.archive;

import org.hippoecm.repository.util.NodeIterable;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * JCR helpers shared between ArchivePlugin and ArchiveInputDialog.
 */
class ArchiveDocumentUtils {

    static final String CONTENT_ROOT = "/content/documents/govscot";
    static final String PUBLICATIONS_PREFIX = "/publications/";
    static final String PUBLICATION_NODE_TYPE = "govscot:Publication";

    private ArchiveDocumentUtils() {}

    /**
     * Returns the public URL path for the document handle.
     * For publications, uses the govscot:slug property.
     * For other documents, strips the content root prefix from the JCR path.
     */
    static String getDocumentPath(Node handleNode) throws RepositoryException {
        Node publicationVariant = getVariantOfType(handleNode, PUBLICATION_NODE_TYPE);
        if (publicationVariant != null && publicationVariant.hasProperty("govscot:slug")) {
            String slug = publicationVariant.getProperty("govscot:slug").getString();
            return PUBLICATIONS_PREFIX + slug;
        }
        String jcrPath = handleNode.getPath();
        if (jcrPath.startsWith(CONTENT_ROOT)) {
            return jcrPath.substring(CONTENT_ROOT.length());
        }
        return jcrPath;
    }

    static boolean isPublication(Node handleNode) throws RepositoryException {
        return getVariantOfType(handleNode, PUBLICATION_NODE_TYPE) != null;
    }

    static Node getVariantOfType(Node handleNode, String nodeType) throws RepositoryException {
        for (Node variant : new NodeIterable(handleNode.getNodes(handleNode.getName()))) {
            if (variant.isNodeType(nodeType)) {
                return variant;
            }
        }
        return null;
    }
}
