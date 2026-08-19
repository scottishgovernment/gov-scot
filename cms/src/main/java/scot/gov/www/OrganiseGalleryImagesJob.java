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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Scheduled job (runs hourly) that moves stray images into subfolders that mirror the
 * structure of the document that references them.
 *
 * <p>Images are looked for directly in {@code /content/gallery}, and anywhere in the
 * {@code /content/gallery/publications} and {@code /content/gallery/govscot} subtrees (each
 * folder itself and all of its descendant folders, at any depth). Only images not yet marked
 * with the {@value #SORTED_PROPERTY} property are considered, so that images moved by a
 * previous run are not repeatedly re-queried and re-processed.
 *
 * <p>The first time an image is seen it is left alone: it is marked with the
 * {@value #ORGANISED_MIXIN} mixin and its {@value #FIRST_SEEN_PROPERTY} property is set to the
 * current time, but {@value #SORTED_PROPERTY} is deliberately left unset so it is still picked
 * up by future runs. Only once {@value #FIRST_SEEN_PROPERTY} is old enough — at least {@value
 * #DEFAULT_MINIMUM_AGE_MINUTES} minutes by default, configurable via the {@value
 * #MINIMUM_AGE_ATTRIBUTE} job attribute — is the image actually resolved and moved, at which
 * point {@value #SORTED_PROPERTY} is set to {@code true}. This means a fresh run of the job
 * never moves anything on its first pass over a batch of new images — it only marks them — and
 * picks them up for an actual move on a later run once they have aged past the minimum.
 *
 * <p>Where an image is moved to depends on whether the document referencing it is a
 * publication (i.e. under {@code /content/documents/govscot/publications/}) or not. Images
 * referenced by a publication document are moved to mirror its path directly under the gallery
 * root, e.g. a document at
 * {@code /content/documents/govscot/publications/type/2026/06/some-report} means an image is
 * moved to {@code /content/gallery/publications/type/2026/06/some-report}. Images referenced by
 * any other kind of document are instead moved to mirror its path nested under
 * {@code /content/gallery/govscot}, e.g. a document at
 * {@code /content/documents/govscot/news/2026/06/some-story} means an image is moved to
 * {@code /content/gallery/govscot/news/2026/06/some-story} — this applies equally to images
 * found directly in the gallery root and to images already somewhere in the
 * {@code /content/gallery/govscot} subtree. Images with no document references are moved to
 * {@code /content/gallery/unreferenced}, split into numbered subfolders (e.g.
 * {@code /content/gallery/unreferenced/000}) of at most {@value #UNREFERENCED_BUCKET_SIZE}
 * images each so that no single folder ends up with an unmanageable number of images. Images
 * referenced more than once, but only ever by pages/sections of the same publication, are moved
 * to that publication's own gallery folder, since all of those references agree on where the
 * image belongs even though there's no single page to mirror. Images referenced more than once
 * where the references don't all agree on a single publication (different publications, a mix
 * of publication and non-publication documents, or several unrelated documents) are instead
 * moved into whichever referenced publication is oldest (by year, then month), or — only if none
 * of the references are under a publication at all — a shared
 * {@code /content/gallery/publications/general} folder. Both same-publication and
 * different-publication multiple-reference cases are logged separately via
 * {@link MultiplyReferencedImageLog}, so examples of each can be found independently.
 *
 * <p>Each page found to reference an image that is actually moved is logged separately via
 * {@link MovedImagePageLog}, on its own logger distinct from this job's, so that stream can be
 * filtered or routed independently of the job's general progress logging.
 *
 * <p>Once every image has been moved out of the gallery root, {@code hippostd:foldertype}
 * and {@code hippostd:gallerytype} are cleared on the root so that new images can no longer
 * be created there directly.
 *
 * <p><b>Scalability</b>: images are processed one at a time; saves are batched via two separate
 * {@link SessionSaver}s so a mass first-seen pass doesn't slow down to the pace of actual moves.
 * One (batch size {@value #SAVE_BATCH_SIZE}, throttle delay {@value #SAVE_DELAY_MS} ms) is used
 * for everything else (actual moves, and marks made once an image is old enough to resolve). The
 * other (batch size {@value #FIRST_SEEN_SAVE_BATCH_SIZE}, throttle delay {@value
 * #FIRST_SEEN_SAVE_DELAY_MS} ms) is used only for first-seen marks, which are cheap, uniform
 * writes that can safely use a larger batch and shorter delay.
 *
 * <p><b>Control flag</b> (boolean property under {@code /content/featureflags/}):
 * {@code OrganiseGalleryImagesJob} — set to {@code true} to allow the job to run.  The flag
 * is re-checked every {@value #FLAG_CHECK_INTERVAL} images, so setting it to {@code false}
 * mid-run stops the job cleanly.  On a successful, uninterrupted run the flag is switched back
 * to {@code false} so the job does not keep re-running on its hourly schedule; it must be
 * re-enabled manually to run again.
 */
public class OrganiseGalleryImagesJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(OrganiseGalleryImagesJob.class);

    static final int  SAVE_BATCH_SIZE     = 100;
    static final long SAVE_DELAY_MS       = 1_000L;
    static final int  FLAG_CHECK_INTERVAL = 100;
    static final int  LOG_INTERVAL        = 1_000;

    /**
     * Batch size/delay for the separate {@link SessionSaver} used for first-seen marks
     * ({@link #markFirstSeen}). Those are cheap, uniform writes (a mixin and a date property, no
     * folder resolution or move), so they can safely use a larger batch and a shorter delay than
     * the saver used for actual moves, letting a mass first-seen pass over a large batch of new
     * images complete much faster without reintroducing one huge unthrottled commit.
     */
    static final int  FIRST_SEEN_SAVE_BATCH_SIZE = 1_000;
    static final long FIRST_SEEN_SAVE_DELAY_MS   = 100L;

    static final int UNREFERENCED_BUCKET_SIZE = 250;

    static final String GALLERY_ROOT        = GalleryFolderUtils.GALLERY_ROOT;
    static final String PUBLICATIONS_FOLDER = "publications";
    static final String GENERAL_FOLDER      = "general";
    static final String GOVSCOT_FOLDER      = "govscot";
    static final String UNREFERENCED_FOLDER = "unreferenced";
    static final String DOCUMENTS_PREFIX    = GalleryFolderUtils.DOCUMENTS_PREFIX;

    private static final String HANDLE_NODE_TYPE = "hippo:handle";

    /**
     * Mixin added to an image's {@code hippo:handle} the first time it is seen, so that later
     * runs can query for images that still need organising rather than walking the whole
     * gallery tree.
     */
    static final String ORGANISED_MIXIN      = "govscot:organised";
    static final String SORTED_PROPERTY      = "govscot:sorted";
    static final String FIRST_SEEN_PROPERTY  = "govscot:firstSeenProcessingDateTime";

    /**
     * Name of the job attribute (see {@code hipposched:attributeNames}/{@code
     * hipposched:attributeValues} on this job's scheduler configuration) giving the minimum age,
     * in minutes, an image must have reached (based on {@value #FIRST_SEEN_PROPERTY}) before it
     * is actually resolved and moved, rather than just marked as seen.
     */
    static final String MINIMUM_AGE_ATTRIBUTE = "minimumAgeMinutes";
    static final long DEFAULT_MINIMUM_AGE_MINUTES = 60;

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
            doExecute(session, flag, minimumAgeMillis(context));
        } catch (Exception e) {
            LOG.error("OrganiseGalleryImagesJob: unexpected error", e);
        } finally {
            session.logout();
        }
    }

    /**
     * Reads the {@value #MINIMUM_AGE_ATTRIBUTE} job attribute (minutes), falling back to
     * {@value #DEFAULT_MINIMUM_AGE_MINUTES} if it is absent or not a valid number, and converts
     * it to milliseconds.
     */
    private long minimumAgeMillis(RepositoryJobExecutionContext context) {
        String value = context.getAttribute(MINIMUM_AGE_ATTRIBUTE);
        long minutes = DEFAULT_MINIMUM_AGE_MINUTES;
        if (value != null) {
            try {
                minutes = Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
                LOG.warn("OrganiseGalleryImagesJob: invalid {} attribute value '{}', using default of {} minutes",
                        MINIMUM_AGE_ATTRIBUTE, value, DEFAULT_MINIMUM_AGE_MINUTES);
                minutes = DEFAULT_MINIMUM_AGE_MINUTES;
            }
        }
        return minutes * 60_000L;
    }

    void doExecute(Session session, FeatureFlag flag, long minimumAgeMillis) throws RepositoryException {
        if (!session.nodeExists(GALLERY_ROOT)) {
            LOG.warn("OrganiseGalleryImagesJob: gallery root {} not found, aborting", GALLERY_ROOT);
            return;
        }
        LOG.info("OrganiseGalleryImagesJob: starting");
        Node galleryRoot = session.getNode(GALLERY_ROOT);
        List<Node> imageHandles = collectImageHandles(session);
        LOG.info("OrganiseGalleryImagesJob: found {} images to organise", imageHandles.size());

        SessionSaver saver = new SessionSaver(session, SAVE_BATCH_SIZE, SAVE_DELAY_MS);
        SessionSaver firstSeenSaver = new SessionSaver(session, FIRST_SEEN_SAVE_BATCH_SIZE, FIRST_SEEN_SAVE_DELAY_MS);
        Stats stats = new Stats();
        long startMs = System.currentTimeMillis();

        try {
            walkImages(session, imageHandles, saver, firstSeenSaver, stats, flag, startMs, minimumAgeMillis);
        } catch (JobStoppedException e) {
            firstSeenSaver.forceSave();
            saver.forceSave();
            logStats(stats, imageHandles.size(), startMs);
            LOG.debug("JobStoppedException", e);
            LOG.info("OrganiseGalleryImagesJob: stopped early by operator after {}",
                    formatDuration(System.currentTimeMillis() - startMs));
            return;
        } catch (RepositoryException e) {
            LOG.error("OrganiseGalleryImagesJob: error during processing after {}; saving progress",
                    formatDuration(System.currentTimeMillis() - startMs), e);
            logStats(stats, imageHandles.size(), startMs);
            firstSeenSaver.forceSave();
            saver.forceSave();
            return;
        }

        firstSeenSaver.forceSave();
        saver.forceSave();

        // Prevent future images being created directly in the gallery root
        galleryRoot.setProperty("hippostd:foldertype", EMPTY_STRINGS);
        galleryRoot.setProperty("hippostd:gallerytype", EMPTY_STRINGS);
        session.save();

        removeEmptyPublicationsFolders(session, saver);

        logFinalStats(stats, imageHandles.size(), startMs);

        flag.setEnabled(false);
        session.save();
        LOG.info("OrganiseGalleryImagesJob: disabled {} feature flag after successful run",
                OrganiseGalleryImagesJob.class.getSimpleName());
    }

    // ---- Image walk ------------------------------------------------------------------------

    private void walkImages(Session session, List<Node> imageHandles, SessionSaver saver,
                             SessionSaver firstSeenSaver, Stats stats, FeatureFlag flag, long startMs,
                             long minimumAgeMillis) throws RepositoryException {
        int index = 0;
        for (Node imageHandle : imageHandles) {
            if (index % FLAG_CHECK_INTERVAL == 0 && !flag.isEnabled()) {
                throw new JobStoppedException();
            }
            processImage(session, imageHandle, stats, saver, firstSeenSaver, minimumAgeMillis);
            index++;
            if (index % LOG_INTERVAL == 0) {
                logStats(stats, imageHandles.size(), startMs);
            }
        }
    }

    /**
     * Queries for image handles not yet marked with {@value #ORGANISED_MIXIN}, sitting directly
     * in the gallery root, or anywhere in the publications or govscot folder subtrees (each
     * folder itself and all of its descendant folders).
     */
    private List<Node> collectImageHandles(Session session) throws RepositoryException {
        List<Node> handles = new ArrayList<>();
        handles.addAll(queryUnsortedHandles(session, GALLERY_ROOT, false));

        String publicationsPath = GALLERY_ROOT + "/" + PUBLICATIONS_FOLDER;
        if (session.nodeExists(publicationsPath)) {
            handles.addAll(queryUnsortedHandles(session, publicationsPath, true));
        }

        String govscotPath = GALLERY_ROOT + "/" + GOVSCOT_FOLDER;
        if (session.nodeExists(govscotPath)) {
            handles.addAll(queryUnsortedHandles(session, govscotPath, true));
        }
        return handles;
    }

    private List<Node> queryUnsortedHandles(Session session, String path, boolean recursive)
            throws RepositoryException {
        String descendantAxis = recursive ? "//" : "/";
        String xpath = String.format(
                "/jcr:root%s%selement(*,hippo:handle)[not(@%s)]",
                path, descendantAxis, SORTED_PROPERTY);
        NodeIterator nodes = session.getWorkspace().getQueryManager()
                .createQuery(xpath, Query.XPATH)
                .execute()
                .getNodes();
        List<Node> handles = new ArrayList<>();
        while (nodes.hasNext()) {
            handles.add(nodes.nextNode());
        }
        return handles;
    }

    /**
     * Marks an image handle as organised (its final move has been made, or confirmed already
     * correct) so that future runs no longer query for it.
     */
    private void markOrganised(Node imageHandle) throws RepositoryException {
        if (!imageHandle.isNodeType(ORGANISED_MIXIN)) {
            imageHandle.addMixin(ORGANISED_MIXIN);
        }
        imageHandle.setProperty(SORTED_PROPERTY, true);
    }

    /**
     * Marks an image handle as seen for the first time: adds {@value #ORGANISED_MIXIN} and sets
     * {@value #FIRST_SEEN_PROPERTY} to now, but deliberately leaves {@value #SORTED_PROPERTY}
     * unset so the image is still picked up by future runs once it has aged enough to move.
     */
    private void markFirstSeen(Node imageHandle) throws RepositoryException {
        if (!imageHandle.isNodeType(ORGANISED_MIXIN)) {
            imageHandle.addMixin(ORGANISED_MIXIN);
        }
        imageHandle.setProperty(FIRST_SEEN_PROPERTY, Calendar.getInstance());
    }

    /**
     * True if the image was first seen at least {@code minimumAgeMillis} ago, based on its
     * {@value #FIRST_SEEN_PROPERTY} property.
     */
    private boolean isOldEnoughToMove(Node imageHandle, long minimumAgeMillis) throws RepositoryException {
        Calendar firstSeen = imageHandle.getProperty(FIRST_SEEN_PROPERTY).getDate();
        long ageMillis = System.currentTimeMillis() - firstSeen.getTimeInMillis();
        return ageMillis >= minimumAgeMillis;
    }

    /**
     * Finds the document(s) referencing the given image, derives the target gallery subfolder
     * from the referencing document's handle path, and moves the image there. Images with no
     * references, and images referenced by more than one document that are not all pages or
     * sections of the same publication, are moved to a numbered bucket subfolder of the
     * unreferenced folder.
     */
    private void processImage(Session session, Node imageHandle, Stats stats, SessionSaver saver,
                               SessionSaver firstSeenSaver, long minimumAgeMillis) throws RepositoryException {
        String imagePath = imageHandle.getPath();
        String imageName = imageHandle.getName();
        String imageUuid = imageHandle.getIdentifier();

        if (!imageHandle.hasProperty(FIRST_SEEN_PROPERTY)) {
            markFirstSeen(imageHandle);
            firstSeenSaver.save();
            LOG.info("OrganiseGalleryImagesJob: first seen image {}, deferring until it has aged", imagePath);
            stats.firstSeen++;
            return;
        }

        if (!isOldEnoughToMove(imageHandle, minimumAgeMillis)) {
            stats.pending++;
            return;
        }

        List<Node> references = findReferences(session, imageUuid);

        if (references.isEmpty()) {
            moveToUnreferencedBucket(session, imageHandle, imagePath, imageName, stats, saver, "unreferenced");
            return;
        }

        Set<String> handlePaths = referencingHandlePaths(references, imagePath, imageName, stats);
        if (handlePaths == null) {
            return;
        }

        if (handlePaths.size() > 1) {
            processMultiplyReferencedImage(session, imageHandle, imagePath, imageName, handlePaths, stats, saver);
        } else {
            processSingleReferenceImage(session, imageHandle, imagePath, imageName,
                    handlePaths.iterator().next(), stats, saver);
        }
    }

    /**
     * Groups the given references by the handle of the document they belong to (referencing
     * documents can have several variants — draft/unpublished/published — each potentially
     * containing its own reference to the image, so references are grouped rather than counted
     * directly). Returns {@code null}, having already logged and updated {@code stats}, if a
     * reference's handle ancestor cannot be found.
     */
    private Set<String> referencingHandlePaths(List<Node> references, String imagePath, String imageName,
                                                 Stats stats) throws RepositoryException {
        Set<String> handlePaths = new LinkedHashSet<>();
        for (Node referenceNode : references) {
            Node ancestor = referenceNode;
            while (!ancestor.isNodeType(HANDLE_NODE_TYPE)) {
                if ("/".equals(ancestor.getPath())) {
                    LOG.warn("OrganiseGalleryImagesJob: could not find handle ancestor for reference at {}, skipping image {}",
                            referenceNode.getPath(), imageName);
                    stats.skipped++;
                    return null;
                }
                ancestor = ancestor.getParent();
            }
            handlePaths.add(ancestor.getPath());
        }
        return handlePaths;
    }

    /**
     * Resolves and moves an image referenced by more than one document, delegating to whichever
     * of {@link #moveToSharedPublicationFolder} or {@link #moveToOldestOrGeneralFolder} applies
     * depending on whether all the references agree on a single publication.
     */
    private void processMultiplyReferencedImage(Session session, Node imageHandle, String imagePath,
                                                  String imageName, Set<String> handlePaths, Stats stats,
                                                  SessionSaver saver) throws RepositoryException {
        List<String> sharedPublicationPathElements = sharedPublicationPathElements(handlePaths);
        if (sharedPublicationPathElements.isEmpty()) {
            moveToOldestOrGeneralFolder(session, imageHandle, imagePath, imageName, handlePaths, stats, saver);
        } else {
            moveToSharedPublicationFolder(session, imageHandle, imagePath, imageName, handlePaths,
                    sharedPublicationPathElements, stats, saver);
        }
    }

    /**
     * Not all references are pages/sections of the same publication (could be different
     * publications, a mix of publication and non-publication documents, or several unrelated
     * documents), so there is no single folder they all agree on: file it in whichever
     * referenced publication is oldest, since that's as good a choice as any and at least keeps
     * it out of a generic bucket. Only when none of the references are under a publication at
     * all does it fall back to a shared "general" folder within the publications gallery.
     */
    private void moveToOldestOrGeneralFolder(Session session, Node imageHandle, String imagePath, String imageName,
                                              Set<String> handlePaths, Stats stats, SessionSaver saver)
            throws RepositoryException {
        MultiplyReferencedImageLog.differentPublications(imagePath, handlePaths);
        List<String> oldestPublicationPathElements = oldestPublicationPathElements(handlePaths);
        Node targetFolder = !oldestPublicationPathElements.isEmpty()
                ? GalleryFolderUtils.ensureGalleryFolderForPath(session, oldestPublicationPathElements)
                : GalleryFolderUtils.ensureImagePath(session, List.of(PUBLICATIONS_FOLDER, GENERAL_FOLDER));
        String destinationPath = targetFolder.getPath() + "/" + imageName;
        markOrganised(imageHandle);
        saver.save();
        if (destinationPath.equals(imagePath) || imagePath.startsWith(targetFolder.getPath() + "/")) {
            LOG.info("OrganiseGalleryImagesJob: multiply-referenced image {} is already in {} "
                            + "(referenced by {})",
                    imagePath, destinationPath, handlePaths);
        } else {
            LOG.info("OrganiseGalleryImagesJob: moving multiply-referenced image: {} -> {} "
                            + "(referenced by {})",
                    imagePath, destinationPath, handlePaths);
            moveImage(session, imagePath, destinationPath, saver);
            for (String handlePath : handlePaths) {
                MovedImagePageLog.pageImageMoved(session, handlePath, imagePath, destinationPath);
            }
        }
        stats.multiplyReferenced++;
    }

    /**
     * Referenced more than once within the same publication (either more than once on the same
     * page, or from different pages of the same publication), so there is no single page to move
     * it to: move it to the publication's own gallery folder.
     */
    private void moveToSharedPublicationFolder(Session session, Node imageHandle, String imagePath,
                                                String imageName, Set<String> handlePaths,
                                                List<String> sharedPublicationPathElements, Stats stats,
                                                SessionSaver saver) throws RepositoryException {
        MultiplyReferencedImageLog.samePublication(imagePath, handlePaths);
        Node publicationFolder = GalleryFolderUtils.ensureGalleryFolderForPath(session, sharedPublicationPathElements);
        String destinationPath = publicationFolder.getPath() + "/" + imageName;
        markOrganised(imageHandle);
        saver.save();
        if (destinationPath.equals(imagePath) || imagePath.startsWith(publicationFolder.getPath() + "/")) {
            LOG.info("OrganiseGalleryImagesJob: image {} is used multiple times within the same publication and "
                            + "is already in its gallery folder (referenced by {})",
                    imagePath, handlePaths);
        } else {
            LOG.info("OrganiseGalleryImagesJob: moving image used multiple times within the same publication: "
                            + "{} -> {} (referenced by {})",
                    imagePath, destinationPath, handlePaths);
            moveImage(session, imagePath, destinationPath, saver);
            for (String handlePath : handlePaths) {
                MovedImagePageLog.pageImageMoved(session, handlePath, imagePath, destinationPath);
            }
        }
        stats.moved++;
    }

    /**
     * Resolves and moves an image referenced by exactly one document.
     */
    private void processSingleReferenceImage(Session session, Node imageHandle, String imagePath, String imageName,
                                              String handlePath, Stats stats, SessionSaver saver)
            throws RepositoryException {
        if (!handlePath.startsWith(DOCUMENTS_PREFIX)) {
            LOG.warn("OrganiseGalleryImagesJob: handle path {} is outside expected prefix, skipping image {}",
                    handlePath, imageName);
            stats.skipped++;
            return;
        }

        String destinationPath = targetPathForDocument(handlePath) + "/" + imageName;
        markOrganised(imageHandle);
        saver.save();
        if (destinationPath.equals(imagePath)) {
            LOG.info("OrganiseGalleryImagesJob: image {} is already in the correct location (referenced by {})",
                    imagePath, handlePath);
        } else if (isWithinPublicationFolder(imagePath, handlePath)) {
            LOG.info("OrganiseGalleryImagesJob: image {} is already within its publication's gallery folder, "
                            + "not moving to exact subfolder {} (referenced by {})",
                    imagePath, destinationPath, handlePath);
        } else {
            // Only now, having established the image actually needs to move there, is the
            // exact subfolder resolved/created: doing this any earlier would create it even for
            // images left in place by the branch above, leaving an empty folder behind for the
            // end-of-run cleanup to immediately remove again.
            targetFolderForDocument(session, handlePath);
            LOG.info("OrganiseGalleryImagesJob: moving image: {} -> {} (referenced by {})",
                    imagePath, destinationPath, handlePath);
            moveImage(session, imagePath, destinationPath, saver);
            MovedImagePageLog.pageImageMoved(session, handlePath, imagePath, destinationPath);
        }
        stats.moved++;
    }

    /**
     * Moves an image to the next numbered unreferenced-bucket subfolder, used both for images
     * with no references at all and for multiply-referenced images that can't be resolved to a
     * single publication's folder. {@code reason} is folded into the log message so the two
     * cases remain distinguishable.
     */
    private void moveToUnreferencedBucket(Session session, Node imageHandle, String imagePath, String imageName,
                                           Stats stats, SessionSaver saver, String reason) throws RepositoryException {
        Node bucketFolder = unreferencedBucketFolder(session, stats.unreferenced);
        String destinationPath = bucketFolder.getPath() + "/" + imageName;
        markOrganised(imageHandle);
        saver.save();
        if (destinationPath.equals(imagePath)) {
            LOG.info("OrganiseGalleryImagesJob: {} image {} is already in the correct location", reason, imagePath);
        } else {
            LOG.info("OrganiseGalleryImagesJob: moving {} image: {} -> {}", reason, imagePath, destinationPath);
            moveImage(session, imagePath, destinationPath, saver);
        }
        stats.unreferenced++;
    }

    /**
     * Resolves the gallery folder that an image referenced by the given document handle path
     * should be moved to. Publication documents mirror their path directly under the gallery
     * root (landing under {@value #PUBLICATIONS_FOLDER}, since that is itself the first element
     * of their path); every other kind of document mirrors its path nested under
     * {@value #GOVSCOT_FOLDER} instead, so that ordinary content images are kept separate from
     * the gallery root and from the publications subtree.
     */
    private Node targetFolderForDocument(Session session, String handlePath) throws RepositoryException {
        return GalleryFolderUtils.isUnderPublications(handlePath)
                ? GalleryFolderUtils.ensureGalleryFolderForDocument(session, handlePath)
                : GalleryFolderUtils.ensureGalleryFolderForDocumentUnder(session, GOVSCOT_FOLDER, handlePath);
    }

    /**
     * Predicts the path {@link #targetFolderForDocument} would resolve/create for the given
     * handle path, without actually creating anything. Used to decide whether an image needs to
     * move at all before paying the cost (and side effect) of ensuring the folder exists.
     */
    private String targetPathForDocument(String handlePath) throws RepositoryException {
        return GalleryFolderUtils.isUnderPublications(handlePath)
                ? GalleryFolderUtils.documentGalleryPath(handlePath)
                : GalleryFolderUtils.documentGalleryPathUnder(GOVSCOT_FOLDER, handlePath);
    }

    /**
     * True if {@code imagePath} already sits somewhere within the gallery folder for the
     * publication itself (i.e. under {@code .../publications/type/year/month/slug}), even if
     * not in the exact subfolder that mirrors {@code handlePath}. Publications can nest
     * documents (e.g. individual pages/sections) under their own slug folder, so an image
     * referenced by such a nested document but already filed somewhere else under the same
     * publication does not need to be shuffled between subfolders.
     */
    private boolean isWithinPublicationFolder(String imagePath, String handlePath) {
        String publicationFolderPath = publicationFolderPath(handlePath);
        return publicationFolderPath != null && imagePath.startsWith(publicationFolderPath + "/");
    }

    /**
     * Returns the gallery folder that mirrors the publication itself (i.e.
     * {@code .../publications/type/year/month/slug}) for the given document handle path, or
     * {@code null} if the handle is not under the publications folder, or is not nested deeply
     * enough (type/year/month/slug) to resolve one.
     */
    private String publicationFolderPath(String handlePath) {
        List<String> pathElements = publicationPathElements(handlePath);
        return pathElements.isEmpty() ? null : GALLERY_ROOT + "/" + String.join("/", pathElements);
    }

    /**
     * Returns the {@code publications/type/year/month/slug} path elements common to every given
     * handle path (i.e. they are all pages/sections of, or the publication document itself for,
     * the same publication), or an empty list if any handle is not under the publications
     * folder, not nested deeply enough to resolve one, or they resolve to different
     * publications.
     */
    private List<String> sharedPublicationPathElements(Set<String> handlePaths) {
        List<String> shared = null;
        for (String handlePath : handlePaths) {
            List<String> pathElements = publicationPathElements(handlePath);
            if (pathElements.isEmpty()) {
                return Collections.emptyList();
            }
            if (shared == null) {
                shared = pathElements;
            } else if (!shared.equals(pathElements)) {
                return Collections.emptyList();
            }
        }
        return shared;
    }

    /**
     * Returns the {@code publications/type/year/month/slug} path elements of whichever of the
     * given handle paths resolves to the oldest publication (by year, then month, both taken
     * from the path), or an empty list if none of them are under the publications folder at
     * all. Handle paths that aren't under a publication are simply ignored rather than
     * disqualifying the result, unlike {@link #sharedPublicationPathElements}.
     */
    private List<String> oldestPublicationPathElements(Set<String> handlePaths) {
        List<String> oldest = Collections.emptyList();
        for (String handlePath : handlePaths) {
            List<String> pathElements = publicationPathElements(handlePath);
            if (!pathElements.isEmpty() && (oldest.isEmpty() || isOlderPublication(pathElements, oldest))) {
                oldest = pathElements;
            }
        }
        return oldest;
    }

    /**
     * True if {@code candidate} (publication path elements: {@code [publications, type, year,
     * month, slug]}) is older than {@code current} — earlier year, or same year and earlier
     * month. Real content doesn't always follow the {@code year/month} convention exactly (e.g.
     * a non-numeric folder used for test content), so a non-numeric year/month is never treated
     * as older than a numeric one, but is preferred over another non-numeric one only if it's
     * the first found (i.e. this never throws).
     */
    private boolean isOlderPublication(List<String> candidate, List<String> current) {
        Integer candidateYear = parseYearOrMonth(candidate.get(2));
        Integer currentYear = parseYearOrMonth(current.get(2));
        if (candidateYear == null) {
            return false;
        }
        if (currentYear == null) {
            return true;
        }
        if (!candidateYear.equals(currentYear)) {
            return candidateYear < currentYear;
        }
        Integer candidateMonth = parseYearOrMonth(candidate.get(3));
        Integer currentMonth = parseYearOrMonth(current.get(3));
        if (candidateMonth == null) {
            return false;
        }
        if (currentMonth == null) {
            return true;
        }
        return candidateMonth < currentMonth;
    }

    private static Integer parseYearOrMonth(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Returns the {@code publications/type/year/month/slug} path elements for the given document
     * handle path, or an empty list if the handle is not under the publications folder, or is
     * not nested deeply enough (type/year/month/slug) to resolve one.
     */
    private List<String> publicationPathElements(String handlePath) {
        if (!handlePath.startsWith(DOCUMENTS_PREFIX)) {
            return Collections.emptyList();
        }
        String[] pathElements = handlePath.substring(DOCUMENTS_PREFIX.length()).split("/");
        if (pathElements.length < 5 || !PUBLICATIONS_FOLDER.equals(pathElements[0])) {
            return Collections.emptyList();
        }
        return Arrays.asList(pathElements).subList(0, 5);
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

    // ---- Empty folder cleanup ---------------------------------------------------------------

    /**
     * Deletes descendant folders of the publications folder that have been left empty by
     * moving their images out. The publications folder itself is never removed, even if it
     * ends up empty.
     */
    private void removeEmptyPublicationsFolders(Session session, SessionSaver saver) throws RepositoryException {
        String publicationsPath = GALLERY_ROOT + "/" + PUBLICATIONS_FOLDER;
        if (!session.nodeExists(publicationsPath)) {
            return;
        }
        pruneEmptySubfolders(session.getNode(publicationsPath), saver);
        saver.forceSave();
    }

    /**
     * Removes descendant folders that hold no images anywhere in their own subtree. Rather than
     * deleting each empty leaf folder individually and then working back up one level at a time,
     * this looks for the highest folder whose entire subtree is free of images and removes it
     * with a single call — JCR cascades a node removal to all of its descendants, so removing
     * ten levels of nested empty folders this way is one repository operation instead of ten.
     * Folders that mix images and empty subfolders are recursed into so that only their empty
     * parts get pruned. The folder passed in is never removed itself, even if it is or becomes
     * empty. Saves are batched via {@code saver} rather than done in one large transaction at
     * the end, consistent with how image moves are saved.
     */
    private void pruneEmptySubfolders(Node folder, SessionSaver saver) throws RepositoryException {
        List<Node> children = new ArrayList<>();
        NodeIterator childIterator = folder.getNodes();
        while (childIterator.hasNext()) {
            children.add(childIterator.nextNode());
        }
        for (Node child : children) {
            if (child.isNodeType(HANDLE_NODE_TYPE)) {
                continue;
            }
            if (isFreeOfImages(child)) {
                LOG.info("OrganiseGalleryImagesJob: removing empty folder {}", child.getPath());
                child.remove();
                saver.save();
            } else {
                pruneEmptySubfolders(child, saver);
            }
        }
    }

    /**
     * True if the given folder's subtree, including the folder itself, contains no images
     * anywhere.
     */
    private boolean isFreeOfImages(Node folder) throws RepositoryException {
        NodeIterator children = folder.getNodes();
        while (children.hasNext()) {
            Node child = children.nextNode();
            if (child.isNodeType(HANDLE_NODE_TYPE) || !isFreeOfImages(child)) {
                return false;
            }
        }
        return true;
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

    /**
     * Logs progress so far: how many of the total images found have been processed, the
     * breakdown by outcome, how long the run has taken, and (based on the average rate so far)
     * an estimate of how much longer it will take to get through the rest.
     */
    private void logStats(Stats stats, int total, long startMs) {
        int processed = stats.moved + stats.unreferenced + stats.multiplyReferenced + stats.skipped
                + stats.firstSeen + stats.pending;
        long elapsedMs = System.currentTimeMillis() - startMs;
        LOG.info("OrganiseGalleryImagesJob: processed={}/{}, moved={}, unreferenced={}, multiplyReferenced={}, "
                        + "skipped={}, firstSeen={}, pending={}, elapsed={}, eta={}",
                processed, total, stats.moved, stats.unreferenced, stats.multiplyReferenced, stats.skipped,
                stats.firstSeen, stats.pending, formatDuration(elapsedMs),
                estimateRemaining(processed, total, elapsedMs));
    }

    /**
     * Estimates the time remaining to process the rest of the images, based on the average
     * processing rate so far. Returns {@code "unknown"} if there is not yet enough data (nothing
     * processed, or no time elapsed) to make an estimate.
     */
    private String estimateRemaining(int processed, int total, long elapsedMs) {
        if (processed <= 0 || elapsedMs <= 0) {
            return "unknown";
        }
        int remaining = total - processed;
        if (remaining <= 0) {
            return "0s";
        }
        long etaMs = (long) (remaining * (elapsedMs / (double) processed));
        return formatDuration(etaMs);
    }

    private void logFinalStats(Stats stats, int total, long startMs) {
        LOG.info("OrganiseGalleryImagesJob: complete in {}", formatDuration(System.currentTimeMillis() - startMs));
        logStats(stats, total, startMs);
    }

    private static class Stats {
        int moved              = 0;
        int unreferenced       = 0;
        int multiplyReferenced = 0;
        int skipped            = 0;
        int firstSeen          = 0;
        int pending            = 0;
    }

    private static class JobStoppedException extends RuntimeException {
        JobStoppedException() {
            super("OrganiseGalleryImagesJob stopped by operator (flag disabled)");
        }
    }
}
