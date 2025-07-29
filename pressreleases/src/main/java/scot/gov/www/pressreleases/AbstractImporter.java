package scot.gov.www.pressreleases;

import org.onehippo.forge.content.exim.core.ContentMigrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.pressreleases.prgloo.PRGlooClient;
import scot.gov.www.pressreleases.prgloo.PRGlooContentType;
import scot.gov.www.pressreleases.prgloo.PRGlooException;
import scot.gov.www.pressreleases.prgloo.PressReleaseConverter;
import scot.gov.www.pressreleases.health.ImporterStatus;
import scot.gov.www.pressreleases.health.ImporterStatusUpdater;
import scot.gov.www.pressreleases.prgloo.rest.Change;
import scot.gov.www.pressreleases.prgloo.rest.ChangeHistory;
import scot.gov.www.pressreleases.prgloo.rest.ChangeType;
import scot.gov.www.pressreleases.prgloo.rest.PressReleaseItem;
import scot.gov.www.pressreleases.sink.PressReleaseSink;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

public abstract class AbstractImporter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractImporter.class);

    ImporterStatusUpdater statusUpdater = new ImporterStatusUpdater();

    Session session;

    PRGlooClient prgloo;

    public AbstractImporter(Session session) {
        this.session = session;
        prgloo = new PRGlooClient();
    }

    void doImport() throws RepositoryException {
        ImporterStatus importerStatus = statusUpdater.getStatus(importerName(), session);
        importerStatus.setLastrun(ZonedDateTime.now());

        try {
            List<Change> changes = fetchChanges(getFetchTime(importerStatus));
            processPressReleases(changes, prgloo, sink());
            importerStatus.setLastSuccessfulRun(importerStatus.getLastrun());
            importerStatus.setMessage("");
        } catch (PressReleaseImporterException e) {
            LOG.error("Exception thrown", e);
            importerStatus.setSuccess(false);
            importerStatus.setMessage(e.getMessage());
        } finally {
            statusUpdater.saveStatus(importerStatus, session);
            session.save();
        }
    }

    Instant getFetchTime(ImporterStatus importerStatus) {
        Instant lastSuccessfulRun = importerStatus.getLastSuccessfulRun().toInstant();
        Instant sevenDaysAgo = Instant.now().minus(Duration.ofDays(7)).plus(Duration.ofHours(1));
        if (lastSuccessfulRun.isBefore(sevenDaysAgo)) {
            return sevenDaysAgo;
        } else {
            return lastSuccessfulRun;
        }
    }

    List<Change> fetchChanges(Instant from) {
        ChangeHistory history = fetchHistoryCall(from);
        List<Change> changes = history.getHistory();
        Collections.reverse(changes);
        LOG.info("Found {} {} changes", changes.size(), prGlooContentType());
        return changes;
    }

    public void processPressReleases(List<Change> changes, PRGlooClient prgloo, PressReleaseSink sink) throws RepositoryException {

        LOG.info("processPressReleases {} changes", changes.size());
        ContentMigrationException contentMigrationException = null;
        List<String> errorMessages = new ArrayList<>();
        for (Change change : changes) {
            try {
                processChange(sink, change, prgloo);
            } catch (ContentMigrationException e) {
                LOG.error("Document checked out by user" + change.getDocId(), e);
                errorMessages.add(e.getMessage());
                contentMigrationException = e;
            }
        }

        if (contentMigrationException != null) {
            throw new PressReleaseImporterException(String.join(", ", errorMessages));
        }
    }

    private void processChange(PressReleaseSink sink, Change change, PRGlooClient prgloo) throws RepositoryException {
        LOG.info("Change doc={} type={} time={}",
                change.getDocId(),
                change.getOperation(),
                change.getTimestamp());

        if (change.getOperation() == ChangeType.UNPUBLISHED) {
            sink.removeDeletedPressRelease(change.getDocId());
            return;
        }

        PressReleaseItem item = getItem(change.getDocId(), prgloo);
        if (item == null) {
            LOG.info("Item not available for processing {}", change.getDocId());
            return;
        }
        sink.acceptPressRelease(new PressReleaseConverter().convert(item));
    }

    private PressReleaseItem getItem(String id, PRGlooClient prgloo) throws PressReleaseImporterException {
        try {
            return prgloo.item(id);
        } catch (PRGlooException e) {
            LOG.warn("Item not found: {}", id, e);
            return null;
        }
    }

    /**
     * the name used to track the status in the repo
     */
    abstract String importerName();

    abstract PRGlooContentType prGlooContentType();

    abstract PressReleaseSink sink();

    abstract ChangeHistory fetchHistoryCall(Instant from) throws PressReleaseImporterException;
}
