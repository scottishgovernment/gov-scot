package scot.gov.www;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Shared logic for ensuring that a chain of {@code hippogallery:stdImageGallery} folders
 * exists under {@value #GALLERY_ROOT} mirroring the path of a document under
 * {@value #DOCUMENTS_PREFIX}. Used by both {@link OrganiseGalleryImagesJob} and the
 * interactive "Create image folder" document action.
 */
public class GalleryFolderUtils {

    public static final String GALLERY_ROOT     = "/content/gallery";
    public static final String DOCUMENTS_PREFIX = "/content/documents/govscot/";

    private static final String[] IMAGE_FOLDER_TYPE  = {"new-image-folder"};
    private static final String[] IMAGE_GALLERY_TYPE = {"hippogallery:imageset"};

    /**
     * Node name Hippo gives a document that represents the index/main document of its own
     * slug folder. When a document handle is named this, the gallery folder should mirror the
     * folder the document lives in, not create an extra nested folder for the document itself.
     */
    private static final String INDEX_NODE_NAME = "index";

    private GalleryFolderUtils() {
        // util class
    }

    /**
     * Ensures a gallery folder exists for the given document handle path, creating any
     * missing folders in the chain. Folders that already exist are left unchanged.
     *
     * @param documentHandlePath the handle path of a document under {@value #DOCUMENTS_PREFIX}
     */
    public static Node ensureGalleryFolderForDocument(Session session, String documentHandlePath)
            throws RepositoryException {
        if (!documentHandlePath.startsWith(DOCUMENTS_PREFIX)) {
            throw new RepositoryException(
                    "Document path " + documentHandlePath + " is outside expected prefix " + DOCUMENTS_PREFIX);
        }
        String relativePath = documentHandlePath.substring(DOCUMENTS_PREFIX.length());
        List<String> pathElements = new ArrayList<>(Arrays.asList(relativePath.split("/")));
        if (!pathElements.isEmpty() && INDEX_NODE_NAME.equals(pathElements.get(pathElements.size() - 1))) {
            pathElements.remove(pathElements.size() - 1);
        }
        return ensureImagePath(session, pathElements);
    }

    /**
     * Ensures a chain of {@code hippogallery:stdImageGallery} folders exists under the gallery
     * root for the given path, creating any missing folders.
     */
    public static Node ensureImagePath(Session session, List<String> path) throws RepositoryException {
        Node parent = session.getNode(GALLERY_ROOT);
        for (String element : path) {
            parent = parent.hasNode(element) ? parent.getNode(element) : createImageFolder(parent, element);
        }
        return parent;
    }

    private static Node createImageFolder(Node parent, String name) throws RepositoryException {
        Node node = parent.addNode(name, "hippogallery:stdImageGallery");
        node.addMixin("hippo:named");
        node.addMixin("mix:referenceable");
        node.setProperty("hippo:name", name);
        node.setProperty("hippostd:foldertype", IMAGE_FOLDER_TYPE);
        node.setProperty("hippostd:gallerytype", IMAGE_GALLERY_TYPE);
        return node;
    }
}
