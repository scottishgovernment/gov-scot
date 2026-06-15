package scot.gov.publications.hippo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.BinaryValue;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.imageprocessing.ImageProcessing;
import scot.gov.imageprocessing.thumbnails.ThumbnailsProvider;
import scot.gov.imageprocessing.thumbnails.ThumbnailsProviderException;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.manifest.Manifest;
import scot.gov.publications.manifest.ManifestEntry;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.util.Exif;
import scot.gov.publications.util.FileType;
import scot.gov.publications.util.MimeTypeUtils;

import javax.jcr.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static scot.gov.publications.hippo.Constants.*;

/**
 * Responsible for uploading documents to hippo.
 */
public class DocumentUploader {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentUploader.class);

    private Session session;

    private Exif exif;

    private HippoUtils hippoUtils = new HippoUtils();

    private HippoPaths hippoPaths;

    private DocumentHelper documentHelper;

    public DocumentUploader(Session session, ImageProcessing imageProcessing, Exif exif) {
        this.session = session;
        this.exif = exif;
        this.hippoPaths = new HippoPaths(session);
        this.documentHelper = new DocumentHelper(session);
    }

    public Map<String, Node> uploadDocuments(
            ZipFile zipFile,
            Node pubFolder,
            Manifest manifest,
            Metadata metadata) throws ApsZipImporterException {
        try {
            return doUploadDocuments(zipFile, pubFolder, manifest, metadata);
        } catch (RepositoryException | IOException | ThumbnailsProviderException e) {
            throw new ApsZipImporterException("Failed to upload documents", e);
        }
    }

    private Map<String, Node> doUploadDocuments(
            ZipFile zipFile,
            Node pubFolder,
            Manifest manifest,
            Metadata metadata) throws RepositoryException, IOException, ApsZipImporterException, ThumbnailsProviderException {

        LOG.debug("Uploading {} documents to {}", manifest.getEntries().size(), pubFolder.getPath());
        Map<String, Node> filenameToDocument = new HashMap<>();
        List<String> path = hippoUtils.pathFromNode(pubFolder);
        path.add("documents");
        Node documentsFolder = hippoPaths.ensurePath(path);
        documentsFolder.setProperty(HIPPOSTD_FOLDERTYPE, new String[]{"new-publication-document-info", "new-publication-documents-folder"});
        SortedMap<String, String> existingDocumentTitles = existingDocumentTitles(documentsFolder);
        hippoUtils.removeChildren(documentsFolder);
        // the removal above is only transient in our session - uploadDocument() now creates each
        // document via FolderWorkflow.add() (through content-exim), which runs against the actual
        // persisted repository state on its own session, so without this save it still sees the old,
        // not-yet-removed handle with the same name and creates a same-name-sibling instead of
        // reusing the slot.
        session.save();

        for (ManifestEntry manifestEntry : manifest.getEntries()) {
            Node docNode = uploadDocument(
                    zipFile,
                    manifest,
                    manifestEntry,
                    documentsFolder,
                    existingDocumentTitles,
                    metadata);
            filenameToDocument.put(manifestEntry.getFilename(), docNode);
        }
        return filenameToDocument;
    }

    private Node uploadDocument(
            ZipFile zipFile,
            Manifest manifest,
            ManifestEntry manifestEntry,
            Node documentsFolder,
            SortedMap<String, String> existingDocumentTitles,
            Metadata metadata) throws RepositoryException, IOException, ApsZipImporterException, ThumbnailsProviderException {

        ZipEntry zipEntry = manifest.findZipEntry(zipFile, manifestEntry);

        if (zipEntry == null) {
            throw new ApsZipImporterException(
                    "Manifest specifies document not present in zip: " + manifestEntry.getFilename());
        }
        String title = getTitle(manifestEntry, existingDocumentTitles);
        String slug = hippoPaths.slugify(title);
        String filename = manifestEntry.getFriendlyFilename();
        String mimeType = detectContentType(filename);

        byte[] bytes = IOUtils.toByteArray(zipFile.getInputStream(zipEntry));
        long pageCount = exif.pageCount(toBinary(bytes), mimeType);

        ContentNode contentNode = new ContentNodes().newDocumentInfoNode(
                title, pageCount, filename, mimeType, new BinaryValue(bytes, mimeType, null));

        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
        String path = documentsFolder.getPath() + "/" + slug;
        String publishedPath = importTask.createOrUpdateDocumentFromVariantContentNode(
                contentNode, "govscot:DocumentInformation", path, "en", title);

        Node documentInfoHandle = session.getNode(publishedPath);
        documentHelper.ensurePublicationState(documentManager, documentInfoHandle, publishedPath, metadata);

        Node documentInfoVariant = hippoUtils.getVariant(documentInfoHandle);
        Node resourceNode = documentInfoVariant.getNode("govscot:document");

        if ("application/pdf".equals(mimeType)) {
            createThumbnails(resourceNode);
        }
        return resourceNode;
    }

    private Binary toBinary(byte[] bytes) throws RepositoryException {
        return session.getValueFactory().createBinary(new ByteArrayInputStream(bytes));
    }

    private String detectContentType(String filename) throws ApsZipImporterException {
        try {
            return MimeTypeUtils.detectContentType(filename);
        } catch (IllegalArgumentException e) {
            throw new ApsZipImporterException(String.format("File has an unsupported file extension: '%s'", filename), e);
        }
    }

    private SortedMap<String, String> existingDocumentTitles(Node documentsFolder) throws RepositoryException {
        SortedMap<String, String> titleByName = new TreeMap<>();
        NodeIterator it = documentsFolder.getNodes();
        while (it.hasNext()) {
            Node handle = it.nextNode();

            if ("hippo:handle".equals(handle.getPrimaryNodeType())) {
                Node documentInfoNode = hippoUtils.mostRecentDraft(handle);
                String title = documentInfoNode.getProperty("govscot:title").getString();
                String name = documentInfoNode.getNode("govscot:document").getProperty("hippo:friendlyFilename").getString();
                titleByName.put(name, title);
            }
        }
        return titleByName;
    }

    /**
     * Determine the friendlyFilename to use for this resource.  If it already exists in Hippo and has a title then use that,
     * otherwise use the title (or friendlyFilename if the title is empty).
     */
    private String getTitle(ManifestEntry manifestEntry, SortedMap<String, String> existingDocumentTitles) {
        String filename = manifestEntry.getFilename();

        if (existingDocumentTitles.containsKey(filename)) {
            String existingtitle = existingDocumentTitles.get(filename);
            LOG.debug("Using existing document title \"{}\" for {}", existingtitle, filename);
            return existingtitle;
        }

        LOG.debug("Using title from manifest \"{}\" for {}", manifestEntry.getTitleOrFilename(), filename);
        return manifestEntry.getTitleOrFilename();
    }

    private void createThumbnails(Node documentNode) throws RepositoryException, IOException, ThumbnailsProviderException {
        Node documentInformationNode = documentNode.getParent();
        Binary data = documentNode.getProperty(JCR_DATA).getBinary();
        String mimeType = documentNode.getProperty(JCR_MIMETYPE).getString();
        String filename = documentNode.getProperty(HIPPO_FILENAME).getString();
        Map<Integer, File> thumbnails = ThumbnailsProvider.thumbnails(data.getStream(), mimeType);

        List<Integer> sortedKeys = new ArrayList<>(thumbnails.keySet());
        Collections.sort(sortedKeys);
        for (Integer size : sortedKeys) {
            File thumbnail = thumbnails.get(size);
            Node resourceNode = documentInformationNode.addNode("govscot:thumbnails", "hippo:resource");
            resourceNode.addMixin("hippo:skipindex");
            Binary binary = session.getValueFactory().createBinary(new FileInputStream(thumbnail));
            String thumbnailFilename = String.format("%s_%s.png", filename, size);
            resourceNode.setProperty(HIPPO_FILENAME, thumbnailFilename);
            resourceNode.setProperty(JCR_DATA, binary);
            resourceNode.setProperty(JCR_MIMETYPE, FileType.PNG.getMimeType());
            resourceNode.setProperty(JCR_LAST_MODIFIED, Calendar.getInstance());
            FileUtils.deleteQuietly(thumbnail);
        }
    }
}
