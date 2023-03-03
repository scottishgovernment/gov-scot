package scot.gov.www.searchjournal;

import org.hippoecm.repository.util.DateTools;
import org.joda.time.DateTime;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.searchjounal.FeatureFlag;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.time.Duration;
import java.util.Calendar;
import java.util.Locale;

/**
 * Delete search journal entries older than 30 days (default, can be configured)
 */
public class SearchJournalCleanupJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(SearchJournalCleanupJob.class);

    private static final String CUTOFF_MILLIS = "cutoffMillis";

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        Session session = context.createSystemSession();
        try {
            FeatureFlag enabled = new FeatureFlag(session, "SearchJournalCleanupJob");
            if (enabled.isEnabled()) {
                long cutoff = getCuttoffMillis(context);
                doExecute(session, cutoff);
            }
        } finally {
            session.logout();
        }
    }

    void doExecute(Session session, long cutoff) throws RepositoryException {
        LOG.info("Running SearchJournalCleanupJob with cutoff {} millis", cutoff);

        NodeIterator nodeIterator = nodesToDelete(session, cutoff);
        int count = 0;
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            deleteEntry(node);
            intermittentSave(session, count++);
        }
        session.save();
        LOG.info("Finished SearchJournalCleanupJob, {} deleted", count);
    }

    long getCuttoffMillis(RepositoryJobExecutionContext context) {
        if (context.getAttributeNames().contains(CUTOFF_MILLIS)) {
            String cutoffString = context.getAttribute(CUTOFF_MILLIS);
            try {
                long cutoff = Long.parseLong(cutoffString);
                LOG.info("Using cutoff of {} millis", cutoff);
                return cutoff;
            } catch (NumberFormatException e) {
                LOG.error("Cutoff configured for SearchJournalCleanupJob is not parsable as a long: {}, will use default", cutoffString);
            }
        }

        LOG.info("Using cutoff of 30 days");
        return Duration.ofDays(30).toMillis();
    }

    NodeIterator nodesToDelete(Session session, long cuttoff) throws RepositoryException {
        Query query = query(session, cuttoff);
        query.setLimit(20000);
        QueryResult result = query.execute();
        LOG.info("{} old entries to delete", result.getNodes().getSize());
        return result.getNodes();
    }

    void deleteEntry(Node entry) throws RepositoryException {
        Node day = entry.getParent();
        Node month = day.getParent();
        Node year = month.getParent();
        entry.remove();
        deleteIfEmpty(day);
        deleteIfEmpty(month);
        deleteIfEmpty(year);
    }

    void deleteIfEmpty(Node node) throws RepositoryException {
        if (!node.hasNodes()) {
            node.remove();
        }
    }

    void intermittentSave(Session session, int count) throws RepositoryException {
        if (count % 500 == 0) {
            LOG.info("saving, count is {}", count);
            session.save();
        }
    }

    Query query(Session session, long cutoffMillis) throws RepositoryException {
        String template = "//element(*, searchjournal:entry)[@searchjournal:timestamp < %s] order by @searchjournal:timestamp";
        String xpath = String.format(template, DateTools.createXPathConstraint(session, cutoff(cutoffMillis)));
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        LOG.info("query: {}", xpath);
        query.setLimit(10000);
        return query;
    }

    Calendar cutoff(long cuttoffMillis) {
        return DateTime.now()
                .minus(cuttoffMillis)
                .toCalendar(Locale.getDefault());
    }

}
