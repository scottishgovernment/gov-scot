package scot.gov.publications.hippo;

import org.apache.commons.io.FileUtils;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static scot.gov.publications.hippo.Constants.*;
import static scot.gov.publications.hippo.Constants.JCR_LAST_MODIFIED;

/**
 * Responsible for uploading documents to hippo.
 */
public class DocumentUploader {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentUploader.class);

    private Session session;

    private Exif exif;

    private HippoUtils hippoUtils = new HippoUtils();

    private HippoNodeFactory nodeFactory;

    private HippoPaths hippoPaths;

    public DocumentUploader(Session session, ImageProcessing imageProcessing, Exif exif) {
        this.session = session;
        this.exif = exif;
        this.hippoPaths = new HippoPaths(session);
        this.nodeFactory = new HippoNodeFactory(session);
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

        LOG.info("Uploading {} documents to {}", manifest.getEntries().size(), pubFolder.getPath());
        Map<String, Node> filenameToDocument = new HashMap<>();
        List<String> path = hippoUtils.pathFromNode(pubFolder);
        path.add("documents");
        Node documentsFolder = hippoPaths.ensurePath(path);
        documentsFolder.setProperty(HIPPOSTD_FOLDERTYPE, new String[]{"new-publication-document-info", "new-publication-documents-folder"});
        SortedMap<String, String> existingDocumentTitles = existingDocumentTitles(documentsFolder);
        hippoUtils.removeChildren(documentsFolder);

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
        Node handle = nodeFactory.newHandle(title, documentsFolder, slug);
        Node documentInfoNode = nodeFactory.newDocumentNode(
                handle,
                slug,
                title,
                "govscot:DocumentInformation",
                metadata.getPublicationDateWithTimezone(),
                metadata.shoudlEmbargo());
        documentInfoNode.setProperty(GOVSCOT_TITLE, title);
        documentInfoNode.setProperty("govscot:accessible", false);
        documentInfoNode.setProperty("govscot:highlighted", false);
        documentInfoNode.setProperty("govscot:size", zipEntry.getSize());

        String mimeType = detectContentType(manifestEntry.getFriendlyFilename());
        Node resourceNode = nodeFactory.newResourceNode(
                documentInfoNode,
                "govscot:document",
                manifestEntry.getFriendlyFilename(),
                mimeType,
                zipFile,
                zipEntry);

        long pageCount = exif.pageCount(resourceNode.getProperty(JCR_DATA).getBinary(), mimeType);
        documentInfoNode.setProperty(GOVSCOT_PAGE_COUNT, pageCount);
        createThumbnails(resourceNode);
        return resourceNode;
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
            LOG.info("Using existing document title \"{}\" for {}", existingtitle, filename);
            return existingtitle;
        }

        LOG.info("Using title from manifest \"{}\" for {}", manifestEntry.getTitleOrFilename(), filename);
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
