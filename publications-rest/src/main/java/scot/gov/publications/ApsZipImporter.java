package scot.gov.publications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.imageprocessing.ImageProcessing;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static scot.gov.publications.hippo.Constants.HIPPOSTD_FOLDERTYPE;

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

    public ImportResult importApsZip(ZipFile zipFile, Publication publication) throws ApsZipImporterException {
        PublicationNodeUpdater publicationNodeUpdater = new PublicationNodeUpdater(session);
        PublicationPageUpdater publicationPageUpdater = new PublicationPageUpdater(session);
        ImageUploader imageUploader = new ImageUploader(session, imageProcessing);
        DocumentUploader documentUploader = new DocumentUploader(session, imageProcessing, exif);

        Node publicationFolder = null;
        Node imagesFolder = null;

        try {
            Manifest manifest = manifestExtractor.extract(zipFile);
            Metadata metadata = metadataExtractor.extract(zipFile);

            // ensure that the publication type is valid
            assertValidPublicationType(session, metadata.mappedPublicationType());

            LOG.debug("Extracted metadata, isbn is {}, title is {}, publication date is {}",
                    metadata.getIsbn(),
                    metadata.getTitle(),
                    metadata.getPublicationDateWithTimezone());

            Node publicationHandle = publicationNodeUpdater.createOrUpdatePublicationNode(metadata, publication);
            publicationFolder = publicationHandle.getParent();

            LOG.debug("publication folder is {}", publicationFolder.getPath());

            Map<String, String> imgMap = imageUploader.createImages(zipFile, publicationFolder);
            Map<String, Node> docMap = documentUploader.uploadDocuments(zipFile, publicationFolder, manifest, metadata);
            if (!imgMap.isEmpty()) {
                imagesFolder = session.getNodeByIdentifier(imgMap.values().iterator().next()).getParent();
            }

            int pageCount = publicationPageUpdater.addPages(
                    zipFile,
                    metadata,
                    publicationFolder,
                    imgMap,
                    docMap);

            publicationFolder = ensureMonthNode(publicationFolder, metadata);
            ensureFolderActions(publicationFolder, metadata.getPublicationType());
            session.save();

            PublicationState publicationState = publicationNodeUpdater.getLastPublicationState();
            return new ImportResult(
                    publicationFolder.getPath(),
                    docMap.size(),
                    pageCount,
                    publicationState.isEmbargoed(),
                    publicationState.isScheduled(),
                    publicationState.isPublished());
        } catch (RepositoryException e) {
            throw new ApsZipImporterException("Failed to save session", e);
        } catch (ApsZipImporterException e) {
            LOG.error("Failed to import publication, removing publication folder", e);
            removePublicationFolderQuietly(publicationFolder, imagesFolder);
            throw e;
        }
    }

    /**
     * Remove a partially imported publication so a failed import does not leave an orphaned
     * folder (and its images) behind in the repository. Any failure while doing so is logged
     * rather than propagated, since it would otherwise mask the original failure.
     */
    void removePublicationFolderQuietly(Node publicationFolder, Node imagesFolder) {
        if (publicationFolder == null) {
            return;
        }

        try {
            if (imagesFolder != null) {
                imagesFolder.remove();
            }
            Session folderSession = publicationFolder.getSession();
            publicationFolder.remove();
            folderSession.save();
        } catch (RepositoryException e) {
            LOG.error("Failed to remove publication folder after exception", e);
        }
    }

    private void assertValidPublicationType(Session session, String type) throws ApsZipImporterException {
        try {
            HippoPaths paths = new HippoPaths(session);
            String slugType = paths.slugify(type, false);
            String xpath = String.format(
                    "/jcr:root/content/documents/govscot/valuelists" +
                            "/publicationTypes/publicationTypes/selection:listitem[selection:key = '%s']", slugType);
            LOG.debug("assertValidPublicationType {}", xpath);
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
     *
     * @return the publication folder node, re-fetched at its new path if it was moved (the Node
     * instance passed in is no longer valid to use after a raw JCR move).
     */
    public Node ensureMonthNode(Node publicationFolder, Metadata metadata) throws RepositoryException {
        List<String> monthPath = pathStrategy.monthFolderPath(metadata);
        Node monthNode = hippoPaths.ensurePath(monthPath);
        Node existingMonthNode = publicationFolder.getParent();
        // if the existing node has a different folder than the new folder then move it into the right folder
        if (!existingMonthNode.getIdentifier().equals(monthNode.getIdentifier())) {
            String oldPath = publicationFolder.getPath();
            String newPath = monthNode.getPath() + "/" + publicationFolder.getName();
            LOG.debug("moving {} to {}", oldPath, newPath);
            session.move(oldPath, newPath);
            updateSlugLookupsForMove(oldPath, newPath);
            return session.getNode(newPath);
        }
        return publicationFolder;
    }

    /**
     * We might have just created a new month or year folder, or moved the publication folder into
     * one - ensure that they have the right actions, which depend on the publication type.
     */
    private void ensureFolderActions(Node publicationFolder, String type) throws RepositoryException {
        Node monthFolder = publicationFolder.getParent();
        Node yearFolder = monthFolder.getParent();
        hippoUtils.setPropertyStrings(publicationFolder, HIPPOSTD_FOLDERTYPE, actions());
        hippoUtils.setPropertyStrings(monthFolder, HIPPOSTD_FOLDERTYPE, publicationActions(type));
        hippoUtils.setPropertyStrings(yearFolder, HIPPOSTD_FOLDERTYPE, actions("new-publication-month-folder"));
    }

    Collection<String> publicationActions(String type) {
        switch (type) {
            case "minutes":
                return actions("new-minutes-folder");
            case "foi-eir-release":
                return actions("new-foi-folder");
            default:
                return actions("new-publication-folder", "new-complex-document-folder");
        }
    }

    Collection<String> actions(String... actions) {
        return Arrays.asList(actions);
    }

    // session.move() above is a raw JCR move, not a FolderWorkflow move, so it never fires the
    // "moveFolder" HippoWorkflowEvent that SlugMaintenanceListener relies on to keep the slug lookup
    // tree in sync (see SlugMaintenanceListener.updateLookupsInFolderForFolderMove /
    // updateUrlLookupsForFolderMove in the cms module). Without this, the preview/live lookup entries
    // for this publication (and its pages, which live underneath it) keep pointing at the old,
    // now-nonexistent path once a type or publication-date change moves it to a new folder - so
    // replicate that same lookup repair here, directly against our own session.
    private void updateSlugLookupsForMove(String oldPath, String newPath) throws RepositoryException {
        String site = sitename(oldPath);
        String fromPath = substringAfter(oldPath, site);
        String toPath = substringAfter(newPath, site);
        String xpath = String.format(
                "/jcr:root/content/urls/%s//element(*, sluglookup:lookup)[jcr:like(@sluglookup:path, '%s/%%')]",
                site, fromPath);
        LOG.debug("updateSlugLookupsForMove: site={} fromPath={} toPath={} xpath={}", site, fromPath, toPath, xpath);
        long matched = hippoUtils.executeXpathQuery(session, xpath, node -> {
            String lookupPath = node.getProperty("sluglookup:path").getString();
            String updatedPath = lookupPath.replace(fromPath, toPath);
            LOG.debug("updateSlugLookupsForMove: updating lookup {} path {} -> {}",
                    node.getPath(), lookupPath, updatedPath);
            node.setProperty("sluglookup:path", updatedPath);
        });
        LOG.debug("updateSlugLookupsForMove: updated {} lookup(s) for move {} -> {}", matched, fromPath, toPath);
    }

    private String sitename(String path) {
        return path.split("/")[3];
    }
}
