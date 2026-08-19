package scot.gov.www;

import org.hippoecm.repository.api.HippoNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Shared logic for ensuring that a chain of {@code hippogallery:stdImageGallery} folders
 * exists under {@value #GALLERY_ROOT} mirroring the path of a document under
 * {@value #DOCUMENTS_PREFIX}. Used by both {@link OrganiseGalleryImagesJob} and the
 * interactive "Create image folder" document action.
 *
 * <p>Each gallery folder's {@code hippo:name} (its display name) is kept in sync with the
 * {@code hippo:name} of the content folder it mirrors, both when the gallery folder is
 * created and whenever it is subsequently found to already exist (e.g. after the content
 * folder has been renamed).
 */
public class GalleryFolderUtils {

    private static final Logger LOG = LoggerFactory.getLogger(GalleryFolderUtils.class);

    public static final String GALLERY_ROOT     = "/content/gallery";
    public static final String DOCUMENTS_PREFIX = "/content/documents/govscot/";

    static final String PUBLICATIONS_FOLDER = "publications";

    /**
     * True if the given document handle path is under the publications folder.
     */
    public static boolean isUnderPublications(String handlePath) {
        return handlePath.startsWith(DOCUMENTS_PREFIX + PUBLICATIONS_FOLDER + "/");
    }

    private static final String[] IMAGE_GALLERY_TYPE = {"hippogallery:imageset"};
    private static final String[] NO_TYPES           = new String[0];

    /**
     * Mixin applied to non-leaf folders in the publications gallery subtree so that the CMS
     * document browser lets editors order their child folders. Leaf folders (the ones that
     * actually hold images) do not need it.
     */
    private static final String ORDER_CHILD_FOLDERS_MIXIN = "frontend:orderChildFolders";

    /**
     * Node name Hippo gives a document that represents the index/main document of its own
     * slug folder. When a document handle is named this, the gallery folder should mirror the
     * folder the document lives in, not create an extra nested folder for the document itself.
     */
    private static final String INDEX_NODE_NAME = "index";

    private static final String NAMED_MIXIN         = "hippo:named";
    private static final String FOLDER_TYPE_PROPERTY  = "hippostd:foldertype";
    private static final String GALLERY_TYPE_PROPERTY = "hippostd:gallerytype";

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
        return ensureGalleryFolderForPath(session, documentPathElements(documentHandlePath));
    }

    /**
     * Ensures a gallery folder exists mirroring the given document handle path, but nested
     * under a named top-level folder rather than directly under the gallery root, creating the
     * top-level folder itself (a plain, non-publications-flavoured folder) and any missing
     * folders in the mirrored chain beneath it. Folders that already exist are left unchanged
     * other than having their display name, mixins and folder/gallery types (re)applied, as
     * described on {@link #ensureGalleryFolderForDocument}.
     *
     * @param topLevelFolder     name of the folder directly under the gallery root to nest under
     * @param documentHandlePath the handle path of a document under {@value #DOCUMENTS_PREFIX}
     */
    public static Node ensureGalleryFolderForDocumentUnder(
            Session session, String topLevelFolder, String documentHandlePath) throws RepositoryException {
        Node topFolder = ensureImagePath(session, List.of(topLevelFolder));
        return ensureImagePath(topFolder, documentPathElements(documentHandlePath), DOCUMENTS_PREFIX);
    }

    /**
     * Predicts the path {@link #ensureGalleryFolderForDocument} would resolve/create for the
     * given document handle path, without creating anything.
     */
    public static String documentGalleryPath(String documentHandlePath) throws RepositoryException {
        return GALLERY_ROOT + "/" + String.join("/", documentPathElements(documentHandlePath));
    }

    /**
     * Predicts the path {@link #ensureGalleryFolderForDocumentUnder} would resolve/create for
     * the given top-level folder and document handle path, without creating anything.
     */
    public static String documentGalleryPathUnder(String topLevelFolder, String documentHandlePath)
            throws RepositoryException {
        return GALLERY_ROOT + "/" + topLevelFolder + "/" + String.join("/", documentPathElements(documentHandlePath));
    }

    private static List<String> documentPathElements(String documentHandlePath) throws RepositoryException {
        if (!documentHandlePath.startsWith(DOCUMENTS_PREFIX)) {
            throw new RepositoryException(
                    "Document path " + documentHandlePath + " is outside expected prefix " + DOCUMENTS_PREFIX);
        }
        String relativePath = documentHandlePath.substring(DOCUMENTS_PREFIX.length());
        List<String> pathElements = new ArrayList<>(Arrays.asList(relativePath.split("/")));
        if (!pathElements.isEmpty() && INDEX_NODE_NAME.equals(pathElements.get(pathElements.size() - 1))) {
            pathElements.remove(pathElements.size() - 1);
        }
        return pathElements;
    }

    /**
     * Ensures a gallery folder exists for the given path of content folder names under
     * {@value #DOCUMENTS_PREFIX} (e.g. the {@code publications/type/year/month/slug} elements
     * of a publication, without any further nested page/section elements), creating any missing
     * folders in the chain. Folders that already exist are left unchanged other than having
     * their display name, mixins and folder/gallery types (re)applied as described on
     * {@link #ensureGalleryFolderForDocument}.
     */
    public static Node ensureGalleryFolderForPath(Session session, List<String> pathElements)
            throws RepositoryException {
        return ensureImagePath(session.getNode(GALLERY_ROOT), pathElements, DOCUMENTS_PREFIX);
    }

    /**
     * Ensures a chain of {@code hippogallery:stdImageGallery} folders exists under the gallery
     * root for the given path, creating any missing folders.
     */
    public static Node ensureImagePath(Session session, List<String> path) throws RepositoryException {
        return ensureImagePath(session.getNode(GALLERY_ROOT), path, null);
    }

    /**
     * Ensures a chain of {@code hippogallery:stdImageGallery} folders exists under
     * {@code startFolder} for the given path, creating any missing folders. When
     * {@code contentBasePath} is given, each folder's display name is taken from the
     * {@code hippo:name} of the corresponding content folder under that base path (falling back
     * to the raw path element if no such content folder exists, or it has no {@code hippo:name}
     * of its own), and is (re)applied even when the gallery folder already exists.
     *
     * <p>Folders in the publications subtree also get {@value #ORDER_CHILD_FOLDERS_MIXIN}
     * added, except for the leaf folder (the last element of {@code path}), which is where
     * images actually end up and so has no child folders to order. This is (re)applied even
     * when the gallery folder already exists.
     *
     * <p>In the publications subtree, only the leaf folder allows images to be uploaded to it;
     * its ancestors (the publications, type, year and month folders) only allow child folders
     * to be created. This is (re)applied even when the gallery folder already exists.
     */
    private static Node ensureImagePath(Node startFolder, List<String> path, String contentBasePath)
            throws RepositoryException {
        Node parent = startFolder;
        Session session = startFolder.getSession();
        StringBuilder contentPath = contentBasePath == null ? null : new StringBuilder(contentBasePath);
        boolean underPublications = !path.isEmpty() && PUBLICATIONS_FOLDER.equals(path.get(0));
        for (int i = 0; i < path.size(); i++) {
            String element = path.get(i);
            String displayName = element;
            if (contentPath != null) {
                contentPath.append(element);
                displayName = displayNameForContentFolder(session, contentPath.toString(), element);
                contentPath.append("/");
            }
            boolean isLeaf = i == path.size() - 1;
            String[] galleryType = underPublications && !isLeaf ? NO_TYPES : IMAGE_GALLERY_TYPE;

            if (parent.hasNode(element)) {
                parent = applyDisplayName(parent.getNode(element), displayName);
            } else {
                Node newFolder = createImageFolder(parent, element, displayName);
                markHasFolders(parent, true);
                parent = newFolder;
            }
            applyFolderTypes(parent, NO_TYPES, galleryType);

            if (underPublications && !isLeaf) {
                ensureOrderChildFoldersMixin(parent);
            }
        }
        return parent;
    }

    private static void ensureOrderChildFoldersMixin(Node folder) throws RepositoryException {
        if (!folder.isNodeType(ORDER_CHILD_FOLDERS_MIXIN)) {
            folder.addMixin(ORDER_CHILD_FOLDERS_MIXIN);
        }
    }

    /**
     * Applies {@code hippostd:foldertype} and {@code hippostd:gallerytype}, but only where the
     * folder's actual node type declares them ({@code hippostd:folder}/{@code hippostd:directory}
     * for the former, {@code hippostd:gallery} for the latter). Folders in the chain are usually
     * all {@code hippogallery:stdImageGallery} (which supports both), but a folder created by
     * some other means with a plain, non-gallery type can otherwise still be found here via
     * {@code parent.hasNode(element)} and re-used rather than created, so this can't assume the
     * property is always applicable — attempting to set an undeclared property throws
     * {@code ConstraintViolationException} and aborts the whole run.
     */
    private static void applyFolderTypes(Node folder, String[] folderType, String[] galleryType)
            throws RepositoryException {
        if (folder.isNodeType("hippostd:folder") || folder.isNodeType("hippostd:directory")) {
            if (!folder.hasProperty(FOLDER_TYPE_PROPERTY)
                    || !Arrays.equals(readStringValues(folder, FOLDER_TYPE_PROPERTY), folderType)) {
                folder.setProperty(FOLDER_TYPE_PROPERTY, folderType);
            }
        } else {
            LOG.warn("GalleryFolderUtils: {} is not a hippostd:folder/hippostd:directory, "
                    + "not applying hippostd:foldertype", folder.getPath());
        }
        if (folder.isNodeType("hippostd:gallery")) {
            if (!folder.hasProperty(GALLERY_TYPE_PROPERTY)
                    || !Arrays.equals(readStringValues(folder, GALLERY_TYPE_PROPERTY), galleryType)) {
                folder.setProperty(GALLERY_TYPE_PROPERTY, galleryType);
            }
        } else {
            LOG.warn("GalleryFolderUtils: {} is not a hippostd:gallery, not applying hippostd:gallerytype",
                    folder.getPath());
        }
    }

    private static String[] readStringValues(Node node, String property) throws RepositoryException {
        if (!node.hasProperty(property)) {
            return NO_TYPES;
        }
        Value[] values = node.getProperty(property).getValues();
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = values[i].getString();
        }
        return strings;
    }

    private static String displayNameForContentFolder(Session session, String contentPath, String fallback)
            throws RepositoryException {
        if (!session.nodeExists(contentPath)) {
            return fallback;
        }
        Node contentNode = session.getNode(contentPath);
        if (contentNode.hasProperty(HippoNodeType.HIPPO_NAME)) {
            return contentNode.getProperty(HippoNodeType.HIPPO_NAME).getString();
        }
        return fallback;
    }

    private static Node applyDisplayName(Node folder, String displayName) throws RepositoryException {
        if (!folder.isNodeType(NAMED_MIXIN)) {
            folder.addMixin(NAMED_MIXIN);
        }
        if (!folder.hasProperty(HippoNodeType.HIPPO_NAME)
                || !displayName.equals(folder.getProperty(HippoNodeType.HIPPO_NAME).getString())) {
            folder.setProperty(HippoNodeType.HIPPO_NAME, displayName);
        }
        return folder;
    }

    private static Node createImageFolder(Node parent, String name, String displayName) throws RepositoryException {
        Node node = parent.addNode(name, "hippogallery:stdImageGallery");
        node.addMixin(NAMED_MIXIN);
        node.addMixin("mix:referenceable");
        node.setProperty(HippoNodeType.HIPPO_NAME, displayName);
        markHasFolders(node, false);
        return node;
    }

    /**
     * Sets {@code hippostd:hasfolders}, the boolean Bloomreach's document browser relies on to
     * know whether a folder has child folders without having to query for them. Folders created
     * directly via JCR (as this class does) don't get it maintained for free the way folders
     * created through the standard folder workflow do, so it has to be kept up to date here:
     * a newly created folder starts with no children of its own ({@code false}), and its parent
     * gains a folder child so is marked {@code true}. Only applies where the folder's node type
     * actually declares the property ({@code hippostd:folder} and its subtypes) — a folder found
     * to already exist with some other type doesn't support it, and attempting to set it would
     * throw {@code ConstraintViolationException}.
     */
    private static void markHasFolders(Node folder, boolean hasFolders) throws RepositoryException {
        if (folder.isNodeType("hippostd:folder")) {
            folder.setProperty("hippostd:hasfolders", hasFolders);
        } else {
            LOG.warn("GalleryFolderUtils: {} is not a hippostd:folder, not applying hippostd:hasfolders",
                    folder.getPath());
        }
    }
}
