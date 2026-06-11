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
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Scheduled job that removes unused properties from content nodes across three phases.
 *
 * <ol>
 *   <li><b>featuredItems</b> — walks all nodes under
 *       {@code /content/documents/govscot/featured-items} and removes
 *       {@code govscot:overlayQuote} and {@code govscot:overlayQuoteAttribution}.</li>
 *   <li><b>publicationProps</b> — queries for any non-frozen node carrying any of
 *       {@code govscot:govscoturl}, {@code govscot:rubricId}, {@code govscot:apshash}, or
 *       {@code govscot:wpid} and removes whichever of those properties are present.</li>
 *   <li><b>redirects</b> — walks all nodes under {@code /content/redirects} and removes
 *       any {@code redirects:description} properties found.</li>
 * </ol>
 *
 * <p><b>Control flag</b> (boolean property under {@code /content/featureflags/}):
 * {@code DeleteUnusedPropertiesJob} — set to {@code true} to run the job.  Setting it to
 * {@code false} mid-run stops the job at the next batch boundary.  The job self-disables
 * this flag only on full completion.
 *
 * <p>All saves are batched via {@link SessionSaver} with a throttle delay after each batch.
 */
public class DeleteUnusedPropertiesJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteUnusedPropertiesJob.class);

    static final int  SAVE_BATCH_SIZE = 100;
    static final long SAVE_DELAY_MS   = 1_000L;

    static final String REDIRECTS_ROOT      = "/content/redirects";
    static final String FEATURED_ITEMS_ROOT = "/content/documents/govscot/featured-items";

    static final String PROP_REDIRECTS_DESCRIPTION    = "redirects:description";
    static final String PROP_OVERLAY_QUOTE             = "govscot:overlayQuote";
    static final String PROP_OVERLAY_QUOTE_ATTRIBUTION = "govscot:overlayQuoteAttribution";
    static final String PROP_GOVSCOT_URL               = "govscot:govscoturl";
    static final String PROP_RUBRIC_ID                 = "govscot:rubricId";
    static final String PROP_APS_HASH                  = "govscot:apshash";
    static final String PROP_WP_ID                     = "govscot:wpid";

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
            flag.setEnabled(false);
            session.save();
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
            removeFeaturedItemsProperties(session, saver, stats, flag);
            removePublicationProperties(session, saver, stats, flag);
            removeRedirectsDescriptions(session, saver, stats, flag);
            logProgress(stats);
        } catch (JobStoppedException e) {
            saver.forceSave();
            LOG.error("DeleteUnusedPropertiesJob: stopped early (flag disabled) after removed={}, byProperty={}",
                    stats.removed, stats.byProperty, e);
            return;
        } catch (RepositoryException e) {
            LOG.error("DeleteUnusedPropertiesJob: error during processing after removed={}; saving progress before re-throwing",
                    stats.removed, e);
            saver.forceSave();
            throw e;
        }
        saver.forceSave();
        LOG.info("DeleteUnusedPropertiesJob: complete, removed={}, byProperty={}", stats.removed, stats.byProperty);
        flag.setEnabled(false);
        session.save();
        LOG.info("DeleteUnusedPropertiesJob: self-disabled feature flag");
    }

    // --- Phase 1: /content/documents/govscot/featured-items — remove overlay properties -----

    private void removeFeaturedItemsProperties(Session session, SessionSaver saver,
                                                Stats stats, FeatureFlag flag)
            throws RepositoryException {
        if (!session.nodeExists(FEATURED_ITEMS_ROOT)) {
            LOG.info("DeleteUnusedPropertiesJob: no featured-items root at {}, skipping", FEATURED_ITEMS_ROOT);
            return;
        }
        LOG.info("DeleteUnusedPropertiesJob: starting featuredItems phase from {}", FEATURED_ITEMS_ROOT);
        walkFeaturedItems(session.getNode(FEATURED_ITEMS_ROOT), saver, stats, flag);
        LOG.info("DeleteUnusedPropertiesJob: featuredItems phase complete, removed={}", stats.removed);
    }

    private void walkFeaturedItems(Node node, SessionSaver saver, Stats stats, FeatureFlag flag)
            throws RepositoryException {
        boolean hasOverlayQuote            = node.hasProperty(PROP_OVERLAY_QUOTE);
        boolean hasOverlayQuoteAttribution = node.hasProperty(PROP_OVERLAY_QUOTE_ATTRIBUTION);
        if (hasOverlayQuote || hasOverlayQuoteAttribution) {
            if (hasOverlayQuote) {
                removeProperty(node, PROP_OVERLAY_QUOTE, stats);
            }
            if (hasOverlayQuoteAttribution) {
                removeProperty(node, PROP_OVERLAY_QUOTE_ATTRIBUTION, stats);
            }
            stats.removed++;
            logProgress(stats);
            saver.save();
            checkStopFlag(flag, stats);
        }
        NodeIterator children = node.getNodes();
        while (children.hasNext()) {
            walkFeaturedItems(children.nextNode(), saver, stats, flag);
        }
    }

    // --- Phase 2: publication properties — remove all unused props if present ----------------

    private void removePublicationProperties(Session session, SessionSaver saver,
                                              Stats stats, FeatureFlag flag)
            throws RepositoryException {
        LOG.info("DeleteUnusedPropertiesJob: starting publicationProps phase");

        String xpath = "//*[not(@jcr:primaryType='nt:frozenNode')"
                + " and (@govscot:govscoturl or @govscot:rubricId"
                + " or @govscot:apshash or @govscot:wpid)]";

        NodeIterator nodes = session.getWorkspace().getQueryManager()
                .createQuery(xpath, Query.XPATH)
                .execute()
                .getNodes();

        while (nodes.hasNext()) {
            processPublicationPropertiesNode(nodes.nextNode(), saver, stats, flag);
        }

        LOG.info("DeleteUnusedPropertiesJob: publicationProps phase complete, removed={}", stats.removed);
    }

    private void processPublicationPropertiesNode(Node node, SessionSaver saver,
                                                   Stats stats, FeatureFlag flag)
            throws RepositoryException {
        boolean hasGovscotUrl = node.hasProperty(PROP_GOVSCOT_URL);
        boolean hasRubricId   = node.hasProperty(PROP_RUBRIC_ID);
        boolean hasApsHash    = node.hasProperty(PROP_APS_HASH);
        boolean hasWpId       = node.hasProperty(PROP_WP_ID);

        if (!hasGovscotUrl && !hasRubricId && !hasApsHash && !hasWpId) {
            return;
        }
        if (hasGovscotUrl) {
            removeProperty(node, PROP_GOVSCOT_URL, stats);
        }
        if (hasRubricId) {
            removeProperty(node, PROP_RUBRIC_ID, stats);
        }
        if (hasApsHash) {
            removeProperty(node, PROP_APS_HASH, stats);
        }
        if (hasWpId) {
            removeProperty(node, PROP_WP_ID, stats);
        }
        stats.removed++;
        logProgress(stats);
        saver.save();
        checkStopFlag(flag, stats);
    }

    // --- Phase 3: /content/redirects — remove redirects:description -------------------------

    private void removeRedirectsDescriptions(Session session, SessionSaver saver,
                                              Stats stats, FeatureFlag flag)
            throws RepositoryException {
        if (!session.nodeExists(REDIRECTS_ROOT)) {
            LOG.info("DeleteUnusedPropertiesJob: no redirects root at {}, skipping", REDIRECTS_ROOT);
            return;
        }
        LOG.info("DeleteUnusedPropertiesJob: starting redirects phase from {}", REDIRECTS_ROOT);
        walkRedirects(session.getNode(REDIRECTS_ROOT), saver, stats, flag);
        LOG.info("DeleteUnusedPropertiesJob: redirects phase complete, removed={}", stats.removed);
    }

    private void walkRedirects(Node node, SessionSaver saver, Stats stats, FeatureFlag flag)
            throws RepositoryException {
        if (node.hasProperty(PROP_REDIRECTS_DESCRIPTION)) {
            removeProperty(node, PROP_REDIRECTS_DESCRIPTION, stats);
            stats.removed++;
            logProgress(stats);
            saver.save();
            checkStopFlag(flag, stats);
        }
        NodeIterator children = node.getNodes();
        while (children.hasNext()) {
            walkRedirects(children.nextNode(), saver, stats, flag);
        }
    }

    // --- Shared helpers ---------------------------------------------------------------------

    /**
     * Removes {@code propertyName} from {@code node} and records the removal in {@code stats}.
     * If the node is checked-in (versioned), the direct removal will throw a
     * {@link VersionException}; in that case the node is checked out, the property removed,
     * the session saved, and the node checked back in.  The checkout/checkin path is logged
     * distinctly so it is easy to spot in production logs.
     */
    private void removeProperty(Node node, String propertyName, Stats stats) throws RepositoryException {
        String nodePath = node.getPath();
        try {
            LOG.debug("DeleteUnusedPropertiesJob: removing {} from {}", propertyName, nodePath);
            node.getProperty(propertyName).remove();
        } catch (VersionException e) {
            LOG.trace("Version was checked in", e);
            LOG.info("DeleteUnusedPropertiesJob: node is checked in, using checkout/checkin to remove {} from {}", propertyName, nodePath);
            Session session = node.getSession();
            VersionManager versionManager = session.getWorkspace().getVersionManager();
            versionManager.checkout(nodePath);
            node.getProperty(propertyName).remove();
            session.save();
            versionManager.checkin(nodePath);
            LOG.info("DeleteUnusedPropertiesJob: checkout/checkin complete for {}", nodePath);
        }
        stats.byProperty.merge(propertyName, 1, Integer::sum);
    }

    private void checkStopFlag(FeatureFlag flag, Stats stats) {
        if (stats.removed > 0 && stats.removed % SAVE_BATCH_SIZE == 0 && !flag.isEnabled()) {
            throw new JobStoppedException();
        }
    }

    private void logProgress(Stats stats) {
        if (stats.removed % 1_000 == 0) {
            LOG.info("DeleteUnusedPropertiesJob: nodes processed={}, byProperty={}", stats.removed, stats.byProperty);
        }
    }

    // --- Inner types ------------------------------------------------------------------------

    private static class Stats {
        int removed = 0;
        final Map<String, Integer> byProperty = new HashMap<>();
    }

    private static class JobStoppedException extends RuntimeException {
        JobStoppedException() {
            super("DeleteUnusedPropertiesJob stopped by operator (flag disabled)");
        }
    }
}
