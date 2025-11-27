package scot.gov.www.importer;

import org.onehippo.forge.content.exim.core.ContentMigrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.importer.domain.PressRelease;
import scot.gov.www.importer.health.ImporterStatus;
import scot.gov.www.importer.health.ImporterStatusUpdater;
import scot.gov.www.importer.sink.ContentSink;
import scot.gov.www.importer.sink.DepublishSink;
import scot.gov.www.importer.sink.NewsSink;
import scot.gov.www.importer.sink.PublicationSink;
import scot.gov.www.importer.vuelio.ContentConverter;
import scot.gov.www.importer.vuelio.VuelioClient;
import scot.gov.www.importer.vuelio.VuelioException;
import scot.gov.www.importer.vuelio.rest.ContentItem;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VuelioImporter {

    private static final Logger LOG = LoggerFactory.getLogger(VuelioImporter.class);

    ImporterStatusUpdater statusUpdater = new ImporterStatusUpdater();

    Session session;

    VuelioClient vuelio;

    ImporterStatus importerStatus;

    public VuelioImporter(Session session) {
        this.session = session;
        vuelio = new VuelioClient();
    }

    void doImport() throws RepositoryException {
        importerStatus = statusUpdater.getStatus("vuelio-importer", session);
        importerStatus.setLastrun(ZonedDateTime.now());

        try {
            List<ContentItem> filteredContent = filterContentToProcess(getFetchTime(importerStatus));
            LOG.info("content obtained");
            processPressReleases(filteredContent);
            importerStatus.setLastSuccessfulRun(importerStatus.getLastrun());
            importerStatus.setMessage("");

        } catch (VuelioImporterException e) {
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
        Instant now = Instant.now().minus(7, ChronoUnit.DAYS);
        if (lastSuccessfulRun.isBefore(now)) {
            return now;
        } else {
            return lastSuccessfulRun;
        }
    }

    List<ContentItem> filterContentToProcess(Instant from) {
        List<ContentItem> results = fetchContent()
                .stream()
                .filter(c -> c.updatedSinceLastRun(from) &&
                        (c.isWebPublishContent() || c.isDeleted() || !c.isPublished()))
                .collect(Collectors.toList());
        Collections.reverse(results);
        LOG.info("Found {} results to process", results.size());
        return results;
    }

    public void processPressReleases(List<ContentItem> contentItems) throws RepositoryException {

        LOG.info("processVuelioContent {} changes", contentItems.size());
        ContentMigrationException contentMigrationException = null;
        List<String> errorMessages = new ArrayList<>();
        for (ContentItem item : contentItems) {
            try {
                processContent(item);
            } catch (ContentMigrationException e) {
                LOG.error("Document checked out by user" + item.getId(), e);
                errorMessages.add(e.getMessage());
                contentMigrationException = e;
            }
        }

        if (contentMigrationException != null) {
            throw new VuelioImporterException(String.join(", ", errorMessages));
        }
    }

    private void processContent(ContentItem contentItem) throws RepositoryException {
        LOG.info("Doc={} metadata={} time={}",
                contentItem.getId(),
                contentItem.getMetadata(),
                contentItem.getDateModified());

        ContentSink sink = getSink(contentItem);
        if (sink == null) {
            LOG.error("cant get a sink for {} {}, unsupported format", contentItem.getId(), contentItem.getHeadLine());
            return;
        }

        if (contentItem.isDeleted() || !contentItem.isPublished()) {
            sink.removeDeletedPressRelease(contentItem.getId());
        } else {
            sink.acceptPressRelease(new ContentConverter().convert(contentItem));
        }
    }

    private List<ContentItem> fetchContent() {
        try {
            return vuelio.content();
        } catch (VuelioException e) {
            throw new VuelioImporterException("failed to fetch content from API", e);
        }
    }

    private ContentSink getSink(ContentItem contentItem) {

        if (contentItem.isDeleted()) {
            new DepublishSink(session);
        }

        if (contentItem.isNews() || contentItem.isStagingNews()) {
            return new NewsSink(session);
        }

        if (contentItem.isCorrespondence()) {
            return new PublicationSink(
                    session, "govscot:Publication", "correspondence","new-publication-folder");
        }

        if (contentItem.isSpeech()) {
            return new PublicationSink(
                    session,
                    "govscot:SpeechOrStatement",
                    "speech-statement",
                    "new-speech-or-statement-folder");
        }
        return null;
    }

}
