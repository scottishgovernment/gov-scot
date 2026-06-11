package scot.gov.www.searchjournal;

import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.searchjournal.JournalEntrySource;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Registers the gov.scot implementation of {@link JournalEntrySource} with the
 * {@link HippoServiceRegistry} so that {@code SearchJournalEventListener} can use it.
 */
public class GovJournalEntrySourceModule implements DaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(GovJournalEntrySourceModule.class);

    private GovJournalEntrySource journalEntrySource;

    @Override
    public void initialize(Session session) throws RepositoryException {
        journalEntrySource = new GovJournalEntrySource(session);
        HippoServiceRegistry.registerService(journalEntrySource, JournalEntrySource.class);
        LOG.info("Registered GovJournalEntrySource");
    }

    @Override
    public void shutdown() {
        if (journalEntrySource != null) {
            HippoServiceRegistry.unregisterService(journalEntrySource, JournalEntrySource.class);
        }
    }
}
