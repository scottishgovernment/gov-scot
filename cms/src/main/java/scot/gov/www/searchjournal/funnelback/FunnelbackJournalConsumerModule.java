package scot.gov.www.searchjournal.funnelback;

import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.modules.DaemonModule;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.journal.*;
import scot.gov.publishing.journal.funnelback.FunnelbackIndexer;
import scot.gov.publishing.journal.funnelback.FunnelbackIndexerFactory;
import scot.gov.publishing.journal.funnelback.FunnelbackJournalPosition;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * {@link DaemonModule} that registers a FunnelbackIndexer-backed {@link JournalConsumerFactory}
 * with the {@link HippoServiceRegistry} so that {@code JournalReconciliationLoop} can
 * discover it at runtime.
 *
 * <p>Within a single job execution, the same {@link FunnelbackIndexer} connection is shared
 * between the {@link JournalConsumer} and the {@link JournalPositionSource} to avoid
 * creating redundant HTTP clients.
 */
public class FunnelbackJournalConsumerModule implements DaemonModule, JournalConsumerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackJournalConsumerModule.class);

    /** The Funnelback collection used to store the journal position. */
    private static final String POSITION_COLLECTION = "ds-journal-push";

    /** The URL key used within the position collection to store the journal position. */
    private static final String POSITION_KEY = "https://www.gov.scot/journalposition";

    /** The site recorded on gov.scot's own journal entries; mounted at the local HST root with no path segment. */
    private static final String ROOT_SITE = "gov";

    private static final String ERROR_RATE_ATTRIBUTE = "errorRate";

    /**
     * Holds the FunnelbackIndexer instance created by {@link #newConsumer} so that
     * {@link #newPositionSource} can reuse the same connection within one execution.
     * Access is single-threaded (scheduler jobs do not run concurrently by default).
     */
    private FunnelbackIndexer currentFunnelback;

    @Override
    public void initialize(Session session) throws RepositoryException {
        HippoServiceRegistry.registerService(this, JournalConsumerFactory.class);
        LOG.info("FunnelbackJournalConsumerModule registered");
    }

    @Override
    public void shutdown() {
        HippoServiceRegistry.unregisterService(this, JournalConsumerFactory.class);
    }

    @Override
    public JournalConsumer newConsumer(RepositoryJobExecutionContext context, Session session) throws JournalConsumerFactoryException {
        FunnelbackIndexer funnelback = FunnelbackIndexerFactory.newFunnelback(POSITION_COLLECTION, POSITION_KEY, errorRate(context));
        if (funnelback == null) {
            throw new JournalConsumerFactoryException("No FunnelbackIndexer token configured");
        }
        currentFunnelback = funnelback;
        SiteContentFetcher siteContentFetcher = new SiteContentFetcher(ROOT_SITE);
        if (!siteContentFetcher.isPingResponding()) {
            LOG.error("SiteContentFetcher ping check failed, local site is not responding");
            throw new JournalConsumerFactoryException("SiteContentFetcher is not ready");
        }
        LOG.debug("SiteContentFetcher ping check succeeded, local site is responding");
        return new FunnelbackJournalConsumer(funnelback, siteContentFetcher, session);
    }

    @Override
    public JournalPositionSource newPositionSource(RepositoryJobExecutionContext context, Session session) throws JournalConsumerFactoryException {
        if (currentFunnelback == null) {
            throw new JournalConsumerFactoryException("newConsumer() must be called before newPositionSource()");
        }
        return new FunnelbackJournalPosition(currentFunnelback);
    }

    private static double errorRate(RepositoryJobExecutionContext context) {
        return context.getAttributeNames().contains(ERROR_RATE_ATTRIBUTE)
                ? Double.parseDouble(context.getAttribute(ERROR_RATE_ATTRIBUTE)) : 0.0;
    }
}
