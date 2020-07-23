package scot.gov.publications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.imageprocessing.ImageProcessing;
import scot.gov.publications.hippo.DocumentUploader;
import scot.gov.publications.hippo.HippoUtils;
import scot.gov.publications.hippo.ImageUploader;
import scot.gov.publications.hippo.pages.PublicationPageUpdater;
import scot.gov.publications.hippo.*;
import scot.gov.publications.manifest.Manifest;
import scot.gov.publications.manifest.ManifestExtractor;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.metadata.MetadataExtractor;
import scot.gov.publications.repo.Publication;
import scot.gov.publications.util.Exif;
import scot.gov.publications.util.ExifProcessImpl;

import javax.jcr.*;
import javax.jcr.query.Query;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

public class ApsZipImporter {

    private static final Logger LOG = LoggerFactory.getLogger(scot.gov.publications.ApsZipImporter.class);

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    ManifestExtractor manifestExtractor = new ManifestExtractor();

    MetadataExtractor metadataExtractor = new MetadataExtractor();

    PublicationPathStrategy pathStrategy;

    HippoPaths hippoPaths;

    Exif exif = new ExifProcessImpl();

    ImageProcessing imageProcessing;

    public ApsZipImporter(Session session) {
        this.session = session;
        this.pathStrategy = new PublicationPathStrategy(session);
        hippoPaths = new HippoPaths(session);
    }

    public String importApsZip(ZipFile zipFile, Publication publication) throws ApsZipImporterException {
        PublicationNodeUpdater publicationNodeUpdater = new PublicationNodeUpdater(session);
        PublicationPageUpdater publicationPageUpdater = new PublicationPageUpdater(session);
        ImageUploader imageUploader = new ImageUploader(session, imageProcessing);
        DocumentUploader documentUploader = new DocumentUploader(session, imageProcessing, exif);

        try {
            Manifest manifest = manifestExtractor.extract(zipFile);
            Metadata metadata = metadataExtractor.extract(zipFile);

            // ensure that the publication type is valid
            assertValidPublicationType(session, metadata.mappedPublicationType());

            LOG.info("Extracted metadata, isbn is {}, title is {}, publication date is {}",
                    metadata.getIsbn(),
                    metadata.getTitle(),
                    metadata.getPublicationDateWithTimezone());

            Node publicationHandle = publicationNodeUpdater.createOrUpdatePublicationNode(metadata, publication);
            Node publicationFolder = publicationHandle.getParent();

            LOG.info("publication folder is {}", publicationFolder.getPath());

            Map<String, String> imgMap = imageUploader.createImages(zipFile, publicationFolder);
            Map<String, Node> docMap = documentUploader.uploadDocuments(zipFile, publicationFolder, manifest, metadata);
            publicationPageUpdater.addPages(
                    zipFile,
                    metadata,
                    publicationFolder,
                    imgMap,
                    docMap);

            ensureMonthNode(publicationFolder, metadata);
            session.save();
            return publicationFolder.getPath();
        } catch (RepositoryException e) {
            throw new ApsZipImporterException("Failed to save session", e);
        }
    }

    private void assertValidPublicationType(Session session, String type) throws ApsZipImporterException {
        try {
            HippoPaths paths = new HippoPaths(session);
            String slugType = paths.slugify(type, false);
            String xpath = String.format(
                    "/jcr:root/content/documents/govscot/valuelists" +
                            "/publicationTypes/publicationTypes/selection:listitem[selection:key = '%s']", slugType);
            LOG.info("assertValidPublicationType {}", xpath);
            Node typeNode = hippoUtils.findOneQuery(session, xpath, Query.XPATH);
            if (typeNode == null) {
                throw new ApsZipImporterException("Unrecognised publication type:" + type);
            }
        } catch (RepositoryException e) {
            throw new ApsZipImporterException("Unable to fetch publication types", e);
        }
    }

    /**
     * Ensure that this publications folder is in the right place.  The folder is based on its type and publicaiton
     * date and so if either changed since the last time the publication was uploaded then it might have to be moved.
     */
    public void ensureMonthNode(Node publicationFolder, Metadata metadata) throws RepositoryException {
        List<String> monthPath = pathStrategy.monthFolderPath(metadata);
        Node monthNode = hippoPaths.ensurePath(monthPath);
        Node existingMonthNode = publicationFolder.getParent();
        // if the existing node has a different folder than the new folder then move it into the right folder
        if (!existingMonthNode.getIdentifier().equals(monthNode.getIdentifier())) {
            String newPath = monthNode.getPath() + "/" + publicationFolder.getName();
            LOG.info("moving {} to {}", publicationFolder.getPath(), newPath);
            session.move(publicationFolder.getPath(), newPath);
        }
    }
}
