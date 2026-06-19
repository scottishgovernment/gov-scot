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
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Scheduled job that removes the unused properties {@code govscot:publicationFilename},
 * {@code govscot:publicationId}, and {@code govscot:publicationUsername} from
 * {@code govscot:Publication} variant nodes under
 * {@code /content/documents/govscot/publications}.
 *
 * <p>The job walks the publications folder tree directly (rather than using a JCR query, which
 * is subject to repository result-size limits).  Intermediate folders (type, year, month) are
 * recursed into; when a folder containing an {@code index} handle is found the walk processes
 * that handle's variants and goes no deeper.
 *
 * <p><b>Control flag</b> (boolean property under {@code /content/featureflags/}):
 * {@code DeleteUnusedPropertiesJob} — set to {@code true} to run the job.  Setting it to
 * {@code false} mid-run stops the job at the next batch boundary.  The job self-disables
 * this flag on full completion.
 *
 * <p>All saves are batched via {@link SessionSaver} with a throttle delay after each batch.
 */
public class DeleteUnusedPropertiesJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteUnusedPropertiesJob.class);

    static final int  SAVE_BATCH_SIZE = 100;
    static final long SAVE_DELAY_MS   = 1_000L;

    static final String PUBLICATIONS_ROOT           = "/content/documents/govscot/publications";
    static final String PROP_PUBLICATION_FILENAME   = "govscot:publicationFilename";
    static final String PROP_PUBLICATION_ID         = "govscot:publicaitonId";
    static final String PROP_PUBLICATION_USERNAME   = "govscot:publicationUsername";


    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {
        Session session = context.createSystemSession();
        try {
            FeatureFlag flag = new FeatureFlag(session, DeleteUnusedPropertiesJob.class.getSimpleName());
            if (!flag.isEnabled()) {
                LOG.info("DeleteUnusedPropertiesJob is not enabled, skipping");
                return;
            }
            doExecute(session, flag);
        } catch (Exception e) {
            LOG.error("DeleteUnusedPropertiesJob: unexpected error", e);
        } finally {
            session.logout();
        }
    }

    void doExecute(Session session, FeatureFlag flag) throws RepositoryException {
        SessionSaver saver = new SessionSaver(session, SAVE_BATCH_SIZE, SAVE_DELAY_MS);
        Stats stats = new Stats();
        try {
            removePublicationFilename(session, saver, stats, flag);
        } catch (JobStoppedException e) {
            saver.forceSave();
            LOG.info("DeleteUnusedPropertiesJob: stopped early (flag disabled), removed={}", stats.removed);
            return;
        } catch (RepositoryException e) {
            LOG.error("DeleteUnusedPropertiesJob: error during processing, removed={}; saving before re-throwing",
                    stats.removed, e);
            saver.forceSave();
            throw e;
        }
        saver.forceSave();
        LOG.info("DeleteUnusedPropertiesJob: complete, removed={}", stats.removed);
        flag.setEnabled(false);
        session.save();
        LOG.info("DeleteUnusedPropertiesJob: self-disabled feature flag");
    }

    private void removePublicationFilename(Session session, SessionSaver saver,
                                            Stats stats, FeatureFlag flag)
            throws RepositoryException {
        if (!session.nodeExists(PUBLICATIONS_ROOT)) {
            LOG.warn("DeleteUnusedPropertiesJob: publications root not found at {}, skipping", PUBLICATIONS_ROOT);
            return;
        }
        LOG.info("DeleteUnusedPropertiesJob: walking {} for govscot:Publication nodes", PUBLICATIONS_ROOT);
        walkPublications(session.getNode(PUBLICATIONS_ROOT), saver, stats, flag);
        LOG.info("DeleteUnusedPropertiesJob: walk complete, removed={}", stats.removed);
    }

    /**
     * Recursively walks the publications folder tree.  If a folder contains an {@code index}
     * child (the publication document handle), the variants under that handle are processed
     * and the walk does not descend further into the folder.  Folders without an {@code index}
     * child are intermediate organisational folders (type, year, month) and are recursed into.
     */
    private void walkPublications(Node folder, SessionSaver saver, Stats stats, FeatureFlag flag)
            throws RepositoryException {
        if (folder.hasNode("index")) {
            NodeIterator variants = folder.getNode("index").getNodes();
            while (variants.hasNext()) {
                Node variant = variants.nextNode();
                if (variant.isNodeType("govscot:Publication")) {
                    processVariant(variant, saver, stats, flag);
                }
            }
            return;
        }

        NodeIterator children = folder.getNodes();
        while (children.hasNext()) {
            Node child = children.nextNode();
            if (child.isNodeType("hippostd:folder")) {
                walkPublications(child, saver, stats, flag);
            }
        }
    }

    private void processVariant(Node variant, SessionSaver saver, Stats stats, FeatureFlag flag)
            throws RepositoryException {
        stats.visited++;
        logProgress(stats);
        boolean changed = removeIfPresent(variant, PROP_PUBLICATION_FILENAME, stats);
        changed |= removeIfPresent(variant, PROP_PUBLICATION_ID, stats);
        changed |= removeIfPresent(variant, PROP_PUBLICATION_USERNAME, stats);
        if (changed) {
            stats.removed++;
            saver.save();
            checkStopFlag(flag, stats);
        }
    }

    // --- Shared helpers ---------------------------------------------------------------------

    /**
     * Removes {@code propertyName} from {@code node} if present.  If the node is checked-in
     * (versioned), checks it out, removes the property, saves, then checks it back in.
     */
    private boolean removeIfPresent(Node node, String propertyName, Stats stats) throws RepositoryException {
        if (!node.hasProperty(propertyName)) {
            return false;
        }
        String nodePath = node.getPath();
        try {
            node.getProperty(propertyName).remove();
        } catch (VersionException e) {
            LOG.trace("Version was checked in", e);
            LOG.info("DeleteUnusedPropertiesJob: node is checked in, using checkout/checkin to remove {} from {}",
                    propertyName, nodePath);
            Session session = node.getSession();
            VersionManager versionManager = session.getWorkspace().getVersionManager();
            versionManager.checkout(nodePath);
            node.getProperty(propertyName).remove();
            session.save();
            versionManager.checkin(nodePath);
            LOG.info("DeleteUnusedPropertiesJob: checkout/checkin complete for {}", nodePath);
        }
        stats.byProperty.merge(propertyName, 1, Integer::sum);
        return true;
    }

    private void checkStopFlag(FeatureFlag flag, Stats stats) {
        if (stats.removed > 0 && stats.removed % SAVE_BATCH_SIZE == 0 && !flag.isEnabled()) {
            throw new JobStoppedException();
        }
    }

    private void logProgress(Stats stats) {
        if (stats.visited % 1_000 == 0) {
            LOG.info("DeleteUnusedPropertiesJob: visited={}, removed={}, byProperty={}",
                    stats.visited, stats.removed, stats.byProperty);
        }
    }

    // --- Inner types ------------------------------------------------------------------------

    private static class Stats {
        int visited  = 0;
        int removed  = 0;
        final Map<String, Integer> byProperty = new HashMap<>();
    }

    private static class JobStoppedException extends RuntimeException {
        JobStoppedException() {
            super("DeleteUnusedPropertiesJob stopped by operator (flag disabled)");
        }
    }
}
