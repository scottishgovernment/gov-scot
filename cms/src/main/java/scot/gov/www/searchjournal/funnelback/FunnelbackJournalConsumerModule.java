package scot.gov.www.searchjournal.funnelback;

import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.modules.DaemonModule;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.journal.JournalConsumer;
import scot.gov.publishing.journal.JournalConsumerFactory;
import scot.gov.publishing.journal.JournalConsumerFactoryException;
import scot.gov.publishing.journal.JournalPositionSource;
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
 * creating redundant HTTP clients. The consumer owns the lifecycle: closing the consumer
 * clears the shared reference.
 */
public class FunnelbackJournalConsumerModule implements DaemonModule, JournalConsumerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackJournalConsumerModule.class);

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
        FunnelbackIndexer funnelback = FunnelbackIndexerFactory.newFunnelback(context);
        if (funnelback == null) {
            throw new JournalConsumerFactoryException("No FunnelbackIndexer token configured");
        }
        currentFunnelback = funnelback;
        FunnelbackJournalConsumer consumer = new FunnelbackJournalConsumer(funnelback, FunnelbackIndexerFactory.newFetcher(context), session) {
            @Override
            public void close() {
                super.close();
                currentFunnelback = null;
            }
        };
        if (!consumer.isReady()) {
            consumer.close();
            throw new JournalConsumerFactoryException("FunnelbackJournalConsumer is not ready");
        }
        return consumer;
    }

    @Override
    public JournalPositionSource newPositionSource(RepositoryJobExecutionContext context, Session session) throws JournalConsumerFactoryException {
        if (currentFunnelback == null) {
            throw new JournalConsumerFactoryException("newConsumer() must be called before newPositionSource()");
        }
        return new FunnelbackJournalPosition(currentFunnelback);
    }
}
