package scot.gov.www;

import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.jcr.FeatureFlag;
import scot.gov.publishing.jcr.SessionSaver;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Scheduled job (runs hourly) that moves stray images into subfolders that mirror the
 * structure of the document that references them.
 *
 * <p>Images are looked for directly in {@code /content/gallery}, directly in
 * {@code /content/gallery/publications}, and directly in each child folder of
 * {@code /content/gallery/publications} (e.g. {@code /content/gallery/publications/agreement}).
 *
 * <p>For example, an image referenced by a document at
 * {@code /content/documents/govscot/news/2026/06/some-story} is moved to
 * {@code /content/gallery/news/2026/06/some-story}.  Images with no document references are
 * moved to {@code /content/gallery/unreferenced}, split into numbered subfolders (e.g.
 * {@code /content/gallery/unreferenced/000}) of at most {@value #UNREFERENCED_BUCKET_SIZE}
 * images each so that no single folder ends up with an unmanageable number of images.
 *
 * <p>Once every image has been moved out of the gallery root, {@code hippostd:foldertype}
 * and {@code hippostd:gallerytype} are cleared on the root so that new images can no longer
 * be created there directly.
 *
 * <p><b>Scalability</b>: images are processed one at a time; saves are batched via
 * {@link SessionSaver} (batch size {@value #SAVE_BATCH_SIZE}, throttle delay
 * {@value #SAVE_DELAY_MS} ms) to avoid overwhelming the repository.
 *
 * <p><b>Control flag</b> (boolean property under {@code /content/featureflags/}):
 * {@code OrganiseGalleryImagesJob} — set to {@code true} to allow the job to run.  The flag
 * is re-checked every {@value #FLAG_CHECK_INTERVAL} images, so setting it to {@code false}
 * mid-run stops the job cleanly.  The flag is left as-is on completion, since the job is
 * expected to keep running on its hourly schedule.
 */
public class OrganiseGalleryImagesJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(OrganiseGalleryImagesJob.class);

    static final int  SAVE_BATCH_SIZE     = 100;
    static final long SAVE_DELAY_MS       = 1_000L;
    static final int  FLAG_CHECK_INTERVAL = 100;
    static final int  LOG_INTERVAL        = 1_000;

    static final int UNREFERENCED_BUCKET_SIZE = 250;

    static final String GALLERY_ROOT        = GalleryFolderUtils.GALLERY_ROOT;
    static final String PUBLICATIONS_FOLDER = "publications";
    static final String UNREFERENCED_FOLDER = "unreferenced";
    static final String DOCUMENTS_PREFIX    = GalleryFolderUtils.DOCUMENTS_PREFIX;

    private static final String[] EMPTY_STRINGS = new String[0];

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {
        Session session = context.createSystemSession();
        try {
            FeatureFlag flag = new FeatureFlag(session, OrganiseGalleryImagesJob.class.getSimpleName());
            if (!flag.isEnabled()) {
                LOG.debug("OrganiseGalleryImagesJob is not enabled, skipping");
                return;
            }
            doExecute(session, flag);
        } catch (Exception e) {
            LOG.error("OrganiseGalleryImagesJob: unexpected error", e);
        } finally {
            session.logout();
        }
    }

    void doExecute(Session session, FeatureFlag flag) throws RepositoryException {
        if (!session.nodeExists(GALLERY_ROOT)) {
            LOG.warn("OrganiseGalleryImagesJob: gallery root {} not found, aborting", GALLERY_ROOT);
            return;
        }

        Node galleryRoot = session.getNode(GALLERY_ROOT);
        List<Node> imageHandles = collectImageHandles(session, galleryRoot);
        LOG.info("OrganiseGalleryImagesJob: found {} images to organise", imageHandles.size());

        SessionSaver saver = new SessionSaver(session, SAVE_BATCH_SIZE, SAVE_DELAY_MS);
        Stats stats = new Stats();
        long startMs = System.currentTimeMillis();

        try {
            walkImages(session, imageHandles, saver, stats, flag);
        } catch (JobStoppedException e) {
            saver.forceSave();
            logStats(stats);
            LOG.debug("JobStoppedException", e);
            LOG.info("OrganiseGalleryImagesJob: stopped early by operator after {}",
                    formatDuration(System.currentTimeMillis() - startMs));
            return;
        } catch (RepositoryException e) {
            LOG.error("OrganiseGalleryImagesJob: error during processing after {}; saving progress",
                    formatDuration(System.currentTimeMillis() - startMs), e);
            logStats(stats);
            saver.forceSave();
            return;
        }

        saver.forceSave();

        // Prevent future images being created directly in the gallery root
        galleryRoot.setProperty("hippostd:foldertype", EMPTY_STRINGS);
        galleryRoot.setProperty("hippostd:gallerytype", EMPTY_STRINGS);
        session.save();

        logFinalStats(stats, System.currentTimeMillis() - startMs);
    }

    // ---- Image walk ------------------------------------------------------------------------

    private void walkImages(Session session, List<Node> imageHandles, SessionSaver saver, Stats stats,
                             FeatureFlag flag) throws RepositoryException {
        int index = 0;
        for (Node imageHandle : imageHandles) {
            if (index % FLAG_CHECK_INTERVAL == 0 && !flag.isEnabled()) {
                throw new JobStoppedException();
            }
            processImage(session, imageHandle, stats, saver);
            index++;
            if (index % LOG_INTERVAL == 0) {
                LOG.info("OrganiseGalleryImagesJob: processed {}/{} images", index, imageHandles.size());
                logStats(stats);
            }
        }
    }

    /**
     * Collects images sitting directly in the gallery root, directly in the publications
     * folder, and directly in each child (type) folder of the publications folder.
     */
    private List<Node> collectImageHandles(Session session, Node galleryRoot) throws RepositoryException {
        List<Node> handles = new ArrayList<>();
        collectImageHandlesIn(galleryRoot, handles);

        String publicationsPath = GALLERY_ROOT + "/" + PUBLICATIONS_FOLDER;
        if (session.nodeExists(publicationsPath)) {
            Node publicationsFolder = session.getNode(publicationsPath);
            collectImageHandlesIn(publicationsFolder, handles);

            NodeIterator typeFolders = publicationsFolder.getNodes();
            while (typeFolders.hasNext()) {
                Node typeFolder = typeFolders.nextNode();
                if (!typeFolder.isNodeType("hippo:handle")) {
                    collectImageHandlesIn(typeFolder, handles);
                }
            }
        }
        return handles;
    }

    private void collectImageHandlesIn(Node folder, List<Node> handles) throws RepositoryException {
        NodeIterator children = folder.getNodes();
        while (children.hasNext()) {
            Node child = children.nextNode();
            if (child.isNodeType("hippo:handle")) {
                handles.add(child);
            }
        }
    }

    /**
     * Finds the first document reference to the given image, derives the target gallery
     * subfolder from the referencing document's handle path, and moves the image there.
     * Images with no references are moved to a numbered bucket subfolder of the unreferenced
     * folder.
     */
    private void processImage(Session session, Node imageHandle, Stats stats, SessionSaver saver)
            throws RepositoryException {
        String imagePath = imageHandle.getPath();
        String imageName = imageHandle.getName();
        String imageUuid = imageHandle.getIdentifier();

        List<Node> references = findReferences(session, imageUuid);

        if (references.isEmpty()) {
            Node bucketFolder = unreferencedBucketFolder(session, stats.unreferenced);
            String destinationPath = bucketFolder.getPath() + "/" + imageName;
            LOG.info("OrganiseGalleryImagesJob: moving unreferenced image: {} -> {}", imagePath, destinationPath);
            moveImage(session, imagePath, destinationPath, saver);
            stats.unreferenced++;
            return;
        }

        // Use the first reference to determine the destination folder
        Node referenceNode = references.get(0);

        // Walk up the ancestor chain to find the hippo:handle for the referencing document
        Node ancestor = referenceNode;
        while (!ancestor.isNodeType("hippo:handle")) {
            if ("/".equals(ancestor.getPath())) {
                LOG.warn("OrganiseGalleryImagesJob: could not find handle ancestor for reference at {}, skipping image {}",
                        referenceNode.getPath(), imageName);
                stats.skipped++;
                return;
            }
            ancestor = ancestor.getParent();
        }

        String handlePath = ancestor.getPath();
        if (!handlePath.startsWith(DOCUMENTS_PREFIX)) {
            LOG.warn("OrganiseGalleryImagesJob: handle path {} is outside expected prefix, skipping image {}",
                    handlePath, imageName);
            stats.skipped++;
            return;
        }

        Node targetFolder = GalleryFolderUtils.ensureGalleryFolderForDocument(session, handlePath);
        String destinationPath = targetFolder.getPath() + "/" + imageName;
        LOG.info("OrganiseGalleryImagesJob: moving image: {} -> {} (referenced by {})",
                imagePath, destinationPath, handlePath);
        moveImage(session, imagePath, destinationPath, saver);
        stats.moved++;
    }

    /**
     * Refreshes the session before saving to avoid InvalidItemStateException caused by
     * concurrent modifications from Hippo's observation listeners. refresh(true) re-reads the
     * current repository state while preserving all pending changes (the move).
     */
    private void moveImage(Session session, String from, String to, SessionSaver saver) throws RepositoryException {
        session.move(from, to);
        session.refresh(true);
        saver.save();
    }

    private List<Node> findReferences(Session session, String imageUuid) throws RepositoryException {
        String xpath = String.format(
                "/jcr:root/content/documents/govscot//element(*,hippo:facetselect)[@hippo:docbase='%s']",
                imageUuid);
        NodeIterator nodes = session.getWorkspace().getQueryManager()
                .createQuery(xpath, Query.XPATH)
                .execute()
                .getNodes();
        List<Node> references = new ArrayList<>();
        while (nodes.hasNext()) {
            references.add(nodes.nextNode());
        }
        return references;
    }

    /**
     * Returns the unreferenced bucket subfolder that the image at the given index should be
     * moved to, creating it if necessary. Images are assigned to buckets in the order they are
     * processed, filling each bucket to UNREFERENCED_BUCKET_SIZE before moving on to the next,
     * so that no bucket folder ends up with more than UNREFERENCED_BUCKET_SIZE images.
     */
    private Node unreferencedBucketFolder(Session session, int unreferencedIndex) throws RepositoryException {
        String bucketName = String.format("%03d", unreferencedIndex / UNREFERENCED_BUCKET_SIZE);
        return GalleryFolderUtils.ensureImagePath(session, List.of(UNREFERENCED_FOLDER, bucketName));
    }

    // ---- Logging -----------------------------------------------------------------------------

    private static String formatDuration(long ms) {
        long hours   = ms / 3_600_000;
        long minutes = (ms % 3_600_000) / 60_000;
        long seconds = (ms % 60_000) / 1_000;
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        }
        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        }
        return String.format("%ds", seconds);
    }

    private void logStats(Stats stats) {
        LOG.info("OrganiseGalleryImagesJob: moved={}, unreferenced={}, skipped={}",
                stats.moved, stats.unreferenced, stats.skipped);
    }

    private void logFinalStats(Stats stats, long elapsedMs) {
        LOG.info("OrganiseGalleryImagesJob: complete in {}", formatDuration(elapsedMs));
        logStats(stats);
    }

    private static class Stats {
        int moved        = 0;
        int unreferenced = 0;
        int skipped      = 0;
    }

    private static class JobStoppedException extends RuntimeException {
        JobStoppedException() {
            super("OrganiseGalleryImagesJob stopped by operator (flag disabled)");
        }
    }
}
