package scot.gov.www.importer;

import org.onehippo.forge.content.exim.core.ContentMigrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static java.util.stream.Collectors.toList;

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
            Instant fetchTime = getFetchTime(importerStatus);
            List<ContentItem> filteredContent = filterContentToProcess(fetchTime);
            processPressReleases(filteredContent);
            importerStatus.setLastSuccessfulRun(importerStatus.getLastrun());
            importerStatus.setMessage("");
        } catch (RuntimeException e) {
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
        List<ContentItem> all = fetchContent(from);
        List<ContentItem> results = all.stream().filter(this::include).collect(toList());
        Collections.reverse(results);
        LOG.info("Found {} results to process", results.size());
        return results;
    }

    boolean include(ContentItem item) {
        if (deletedOrUnpublished(item)) {
            // always process deletions and unpublished
            return true;
        }

        // never process non web published content
        // note deleted things have isWebPublishContent == false and so you have to check the deleted part first
        return item.isWebPublishContent();
    }

    boolean deletedOrUnpublished(ContentItem item) {
        return item.isDeleted() || !item.isPublished();
    }

    public void processPressReleases(List<ContentItem> contentItems) throws RepositoryException {

        LOG.info("processVuelioContent {} changes", contentItems.size());
        Throwable contentMigrationException = null;
        List<String> errorMessages = new ArrayList<>();
        for (ContentItem item : contentItems) {
            try {
                processContent(item);
            } catch (ContentMigrationException e) {
                LOG.error("Document checked out by user" + item.getId(), e);
                errorMessages.add(e.getMessage());
                contentMigrationException = e;
            } catch (RuntimeException e) {
                LOG.error("RuntimeException processing item " + item.getId(), e);
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

    private List<ContentItem> fetchContent(Instant time) {
        try {
            return vuelio.content(time);
        } catch (VuelioException e) {
            throw new VuelioImporterException("failed to fetch content from API", e);
        }
    }

    private ContentSink getSink(ContentItem contentItem) {

        if (deletedOrUnpublished(contentItem)) {
            return new DepublishSink(session);
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
