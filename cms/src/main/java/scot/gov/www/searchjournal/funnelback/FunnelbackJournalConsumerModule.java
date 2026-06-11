package scot.gov.www.searchjournal.funnelback;

import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.modules.DaemonModule;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.searchjournal.JournalConsumer;
import scot.gov.publishing.searchjournal.JournalConsumerSource;
import scot.gov.publishing.searchjournal.JournalPositionSource;
import scot.gov.publishing.searchjournal.funnelback.Funnelback;
import scot.gov.publishing.searchjournal.funnelback.FunnelbackFactory;
import scot.gov.publishing.searchjournal.funnelback.FunnelbackJournalPosition;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * {@link DaemonModule} that registers a Funnelback-backed {@link JournalConsumerSource}
 * with the {@link HippoServiceRegistry} so that {@code JournalReconciliationLoop} can
 * discover it at runtime.
 *
 * <p>Within a single job execution, the same {@link Funnelback} connection is shared
 * between the {@link JournalConsumer} and the {@link JournalPositionSource} to avoid
 * creating redundant HTTP clients. The consumer owns the lifecycle: closing the consumer
 * clears the shared reference.
 */
public class FunnelbackJournalConsumerModule implements DaemonModule, JournalConsumerSource {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackJournalConsumerModule.class);

    /**
     * Holds the Funnelback instance created by {@link #newConsumer} so that
     * {@link #newPositionSource} can reuse the same connection within one execution.
     * Access is single-threaded (scheduler jobs do not run concurrently by default).
     */
    private Funnelback currentFunnelback;

    @Override
    public void initialize(Session session) throws RepositoryException {
        HippoServiceRegistry.registerService(this, JournalConsumerSource.class);
        LOG.info("FunnelbackJournalConsumerModule registered");
    }

    @Override
    public void shutdown() {
        HippoServiceRegistry.unregisterService(this, JournalConsumerSource.class);
    }

    @Override
    public JournalConsumer newConsumer(RepositoryJobExecutionContext context, Session session) {
        Funnelback funnelback = FunnelbackFactory.newFunnelback(context);
        if (funnelback == null) {
            LOG.warn("No Funnelback token configured, skipping");
            return null;
        }
        currentFunnelback = funnelback;
        return new FunnelbackJournalConsumer(funnelback, FunnelbackFactory.newFetcher(context), session) {
            @Override
            public void close() {
                super.close();
                currentFunnelback = null;
            }
        };
    }

    @Override
    public JournalPositionSource newPositionSource(RepositoryJobExecutionContext context, Session session) {
        if (currentFunnelback == null) {
            LOG.warn("newConsumer() must be called before newPositionSource()");
            return null;
        }
        return new FunnelbackJournalPosition(currentFunnelback);
    }
}
