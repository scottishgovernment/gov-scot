package scot.gov.www.searchjournal;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.httpclient.HttpClientSource;
import scot.gov.publishing.searchjournal.FeatureFlag;
import scot.gov.publishing.searchjournal.SearchJournal;
import scot.gov.publishing.searchjournal.SearchJournalEntry;
import scot.gov.searchjournal.FunnelbackMetricRegistry;
import scot.gov.searchjournal.MetricName;
import scot.gov.www.searchjournal.funnelback.*;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Reconciliation loop to read the search journal index index content in funnelback.
 */
public class FunnelbackReconciliationLoop implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackReconciliationLoop.class);

    private static final String BATCH_SIZE_ATTRIBUTE = "maxJournalEntriesToFetch";

    private static final String REQUEST_PAUSE_ATTRIBUTE = "interRequestPause";

    // dictates how often the journal position is stored in funnelback
    private int saveInterval = 100;

    // length of time to pause between requests to funnelback
    private long interRequestPause = 100;

    // the maximum number of journal entries to fetch each time the job runs
    private int maxJournalEntriesToFetch = 2000;

    private RetryPolicy retryPolicy = new RetryPolicy();

    private Counter failureCounter = FunnelbackMetricRegistry.getInstance().counter(MetricName.FAILURES.getName());

    private final Meter failureMeter = FunnelbackMetricRegistry.getInstance().meter(MetricName.FAILURE_RATE.getName());

    private final Timer jobTimer = FunnelbackMetricRegistry.getInstance().timer(MetricName.JOB_TIMES.getName());

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        if (!isReady()) {
            return;
        }
        configure(context);

        Session session = context.createSystemSession();
        try {
            FeatureFlag featureFlag = new FeatureFlag(session, "FunnelbackReconciliationLoop");
            if (featureFlag.isEnabled()) {
                doExecute(context, session, featureFlag);
            }
        } catch (RepositoryException e) {
            LOG.error("RepositoryException during funnelback reconciliation", e);
            throw e;
        } finally {
            session.logout();
        }
    }

    void configure(RepositoryJobExecutionContext context) {
        if (context.getAttributeNames().contains(BATCH_SIZE_ATTRIBUTE)) {
            String maxJournalEntriesToFetchString = context.getAttribute(BATCH_SIZE_ATTRIBUTE);
            try {
                maxJournalEntriesToFetch = Integer.parseInt(maxJournalEntriesToFetchString);
            } catch (NumberFormatException e) {
                LOG.error("Invalid value of {}: \"{}\", defaulting to 2000", BATCH_SIZE_ATTRIBUTE, maxJournalEntriesToFetchString);
            }
        }

        if (context.getAttributeNames().contains(REQUEST_PAUSE_ATTRIBUTE)) {
            String interRequestPauseString = context.getAttribute(REQUEST_PAUSE_ATTRIBUTE);
            try {
                interRequestPause = Long.parseLong(interRequestPauseString);
            } catch (NumberFormatException e) {
                LOG.error("Invalid value of {}: \"{}\", defaulting to 2000", REQUEST_PAUSE_ATTRIBUTE, interRequestPauseString, interRequestPause);
            }
        }
    }

    static boolean isReady() {
        if (!pingUrlResponding()) {
            LOG.warn("Ping url not responding yet");
            return false;
        }

        return true;
    }

    void doExecute(RepositoryJobExecutionContext context, Session session, FeatureFlag featureFlag) throws RepositoryException {
        Funnelback funnelback = FunnelbackFactory.newFunnelback(context);
        CloseableHttpClient httpClient = HttpClientSource.newClient();

        try {
            fetchAndProcessPendingJournalEntries(funnelback, httpClient, session, featureFlag);
        } catch (FunnelbackException e) {
            LOG.error("FunnelbackException during funnelback reconciliation", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                LOG.error("Failed to close http client", e);
            }
            funnelback.close();
        }
    }

    static boolean pingUrlResponding() {
        HttpGet request = new HttpGet("http://localhost:8080/site/ping");
        try (CloseableHttpClient httpClient = HttpClientSource.newClient()) {
            CloseableHttpResponse response = httpClient.execute(request);
            try {
                return response.getStatusLine().getStatusCode() == 200;
            } finally {
                IOUtils.closeQuietly(response, e -> LOG.warn("Failed to close ping request", e));
            }
        } catch (IOException e) {
            LOG.warn("Failed to fetch ping url", e);
        }

        return false;
    }

    void fetchAndProcessPendingJournalEntries(
            Funnelback funnelback,
            CloseableHttpClient httpClient,
            Session session,
            FeatureFlag featureFlag) throws RepositoryException, FunnelbackException {

        SearchJournal journal = new SearchJournal(session);
        JournalPosition journalPosition = funnelback.getJournalPosition();
        if (journalPosition == null) {
            LOG.info("No journal position found ... skipping this run.");
            return;
        }

        LOG.info("Journal position is {}", journalPosition);
        List<SearchJournalEntry> pendingEntries =
                journal.getPendingEntries(journalPosition.getPosition(), journalPosition.getSequence(), maxJournalEntriesToFetch);
        if (pendingEntries.isEmpty()) {
            LOG.info("No journal entries to process");
            return;
        }

        LOG.info("{} pending journal entries to process, journal position is {}", pendingEntries.size(), journalPosition);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Timer.Context timerContext = jobTimer.time();
        int count = processPendingEntries(pendingEntries, funnelback, httpClient, journal, featureFlag);
        timerContext.stop();
        stopWatch.stop();
        LOG.info("reconciliation loop took {} to process {} journal entries", stopWatch.getTime(), count);
    }

    int processPendingEntries(
            List<SearchJournalEntry> pendingEntries,
            Funnelback funnelback,
            CloseableHttpClient httpClient,
            SearchJournal journal,
            FeatureFlag featureFlag) throws FunnelbackException {


        Map<String, List<SearchJournalEntry>> pendingEntriesByUrl = new HashMap<>();
        for (SearchJournalEntry entry : pendingEntries) {
            pendingEntriesByUrl.putIfAbsent(entry.getUrl(), new ArrayList<>());
            pendingEntriesByUrl.get(entry.getUrl()).add(entry);
        }

        int count = 0;
        SearchJournalEntry lastEntry = null;
        for (SearchJournalEntry entry : pendingEntries) {

            if (!featureFlag.isEnabled()) {
                LOG.info("Job has been disabled, finishing early");
                break;
            }

            if (moreRecentEntryForUrl(entry, pendingEntriesByUrl)) {
                LOG.info("more recent entries exits for {}, skipping", entry.getUrl());
            } else {
                processEntry(funnelback, httpClient, journal, entry);
                count++;
                periodicSave(funnelback, entry, count);
            }
            lastEntry = entry;
        }
        if (lastEntry != null) {
            JournalPosition position = new JournalPosition();
            position.setPosition(lastEntry.getTimestamp());
            position.setSequence(lastEntry.getSequence());
            funnelback.storeJournalPosition(position);
        }
        return count;
    }

    boolean moreRecentEntryForUrl(SearchJournalEntry entry, Map<String, List<SearchJournalEntry>> pendingEntriesByUrl) {
        List<SearchJournalEntry> entriesForUrl = pendingEntriesByUrl.get(entry.getUrl());
        SearchJournalEntry mostrecententry = entriesForUrl.get(entriesForUrl.size() - 1);
        return entriesForUrl.size() > 1 && entry.getTimestamp().before(mostrecententry.getTimestamp());
    }

    void processEntry(Funnelback funnelback, CloseableHttpClient httpClient, SearchJournal journal, SearchJournalEntry entry) {
        try {
            doProcessEntry(funnelback, httpClient, entry);
            TimeUnit.MILLISECONDS.sleep(interRequestPause);
        } catch (IOException e) {
            LOG.error("Failed to fetch HTML for journal entry {} ", entry.getUrl(), e);
            handleFailure(entry, journal);
        } catch (FunnelbackException e) {
            LOG.error("Failed index content for {} ", entry.getUrl(), e);
            handleFailure(entry, journal);
        } catch (InterruptedException e) {
            LOG.error("Interrupted while pausing after {}", entry.getUrl(), e);
        }
    }

    void doProcessEntry(Funnelback funnelback, CloseableHttpClient httpClient, SearchJournalEntry entry) throws IOException, FunnelbackException{
        LOG.info("processing {} {} {} {} attempt {}", ((GregorianCalendar)entry.getTimestamp()).toZonedDateTime(), entry.getAction(), entry.getUrl(), entry.getCollection(), entry.getAttempt());
        switch (entry.getAction()) {
            case "depublish":
                funnelback.depublish(entry.getCollection(), entry.getUrl());
                break;
            case "publish":
                String html = getHtml(entry, httpClient);
                if (html != null) {
                    funnelback.publish(entry.getCollection(), entry.getUrl(), html);
                }
                break;
            default:
                LOG.error("Unexpected action {}", entry.getAction());
        }
    }

    void periodicSave(Funnelback funnelback, SearchJournalEntry entry, int count) throws FunnelbackException {
        if (count % saveInterval == 0) {
            JournalPosition journalPosition = new JournalPosition();
            journalPosition.setPosition(entry.getTimestamp());
            journalPosition.setSequence(entry.getSequence());
            funnelback.storeJournalPosition(journalPosition);
        }
    }

    String getLocalUrl(String url) {
        return StringUtils.replace(url, "https://www.gov.scot/", "http://localhost:8080/site/");
    }

    String getHtml(SearchJournalEntry entry, CloseableHttpClient httpClient) throws IOException {
        String localUrl = getLocalUrl(entry.getUrl());
        HttpGet request = new HttpGet(localUrl);
        request.setHeader("X-Forwarded-Host", "www.gov.scot");
        CloseableHttpResponse response = httpClient.execute(request);

        try {
            // if this is not a OK response then LOG and return null
            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.error("{} fetching {}", response.getStatusLine().getStatusCode(), localUrl);
                return null;
            } else {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            }
        } finally {
            response.close();
        }
    }

    void handleFailure(SearchJournalEntry entry, SearchJournal journal) {
        try {
            doHandleFailure(entry, journal);
        } catch (RepositoryException e) {
            LOG.error("RepositoryException trying to create a new journal entry for a failure. ", e);
        }
    }

    void doHandleFailure(SearchJournalEntry entry, SearchJournal journal) throws RepositoryException {
        if (!retryPolicy.shouldRetry(entry)) {
            failureCounter.inc();
            failureMeter.mark();
            LOG.error("entry has reached max attempts: {}, {}", entry.getAction(), entry.getUrl());
            return;
        }

        LOG.info("reconciliation failed {} {} {}, attempt {}",
                entry.getAction(), entry.getCollection(), entry.getUrl(), entry.getAttempt());
        long newAttempt = entry.getAttempt() + 1;
        SearchJournalEntry newEntry = new SearchJournalEntry();
        newEntry.setUrl(entry.getUrl());
        newEntry.setAction(entry.getAction());
        newEntry.setCollection(entry.getCollection());
        newEntry.setAttempt(newAttempt);
        newEntry.setTimestamp(getNewTimestamp(entry));
        journal.record(newEntry);
    }

    Calendar getNewTimestamp(SearchJournalEntry entry) {
        long backoff = retryPolicy.getBackoffPeriodInMillis(entry);
        long newTime = System.currentTimeMillis() + backoff;
        Calendar newTimestamp = Calendar.getInstance();
        newTimestamp.setTimeInMillis(newTime);
        return newTimestamp;
    }

}