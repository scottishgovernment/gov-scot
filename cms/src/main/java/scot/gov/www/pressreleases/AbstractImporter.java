package scot.gov.www.pressreleases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import scot.gov.pressreleases.ImporterStatus;
import scot.gov.pressreleases.ImporterStatusUpdater;
import scot.gov.www.pressreleases.prgloo.*;
import scot.gov.www.pressreleases.prgloo.rest.Change;
import scot.gov.www.pressreleases.prgloo.rest.ChangeHistory;
import scot.gov.www.pressreleases.prgloo.rest.ChangeType;
import scot.gov.www.pressreleases.prgloo.rest.PressReleaseItem;
import scot.gov.www.pressreleases.sink.PressReleaseSink;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractImporter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractImporter.class);

    ImporterStatusUpdater statusUpdater = new ImporterStatusUpdater();

    Session session;

    PRGloo prgloo;

    public AbstractImporter(Session session) {
        this.session = session;
        prgloo = PRGlooFactory.newInstance();
    }

    void doImport() throws RepositoryException {
        ImporterStatus importerStatus = statusUpdater.getStatus(importerName(), session);
        importerStatus.setLastrun(ZonedDateTime.now());

        try {
            List<Change> changes = fetchChanges(importerStatus.getLastSuccessfulRun().toInstant());
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

    List<Change> fetchChanges(Instant from) {
        Response<ChangeHistory> response = Calls.call(fetchHistoryCall(from));
        if (!response.isSuccessful()) {
            throw new PressReleaseImporterException(unsuccessfulMessage(prGlooContentType(), response));
        }
        ChangeHistory history = response.body();
        List<Change> changes = history.getHistory();
        Collections.reverse(changes);
        LOG.info("Found {} {} changes", changes.size(), prGlooContentType());
        return changes;
    }

    private String unsuccessfulMessage(PRGlooContentType contentType, Response response) {
        try {
            String responseBody = response.errorBody().string();
            return String.format("Could not get %s changes: %s", contentType, responseBody);
        } catch (IOException e) {
            LOG.error("IOException reading error response body", e);
            return String.format("Could not get %s changes, failed to read response body", contentType);
        }
    }

    public void processPressReleases(List<Change> changes, PRGloo prgloo, PressReleaseSink sink) throws RepositoryException {
        Set<String> seen = new HashSet<>();
        for (Change change : changes) {
            if (!seen.contains(change.getDocId())) {
                processChange(sink, change, prgloo);
            }
            seen.add(change.getDocId());
        }
    }

    private void processChange(PressReleaseSink sink, Change change, PRGloo prgloo) throws RepositoryException {
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
        LOG.info("item {}", item);
        sink.acceptPressRelease(new PressReleaseConverter().convert(item));
    }

    private PressReleaseItem getItem(String id, PRGloo prgloo) {
        Response<PressReleaseItem> response = Calls.call(prgloo.item(id));
        if (!response.isSuccessful()) {
            LOG.warn("Item not found: {}", id);
            return null;
        }
        return response.body();
    }

    /**
     * the name used to track the status in the repo
     */
    abstract String importerName();

    abstract PRGlooContentType prGlooContentType();

    abstract PressReleaseSink sink();

    abstract Call<ChangeHistory> fetchHistoryCall(Instant from);
}
