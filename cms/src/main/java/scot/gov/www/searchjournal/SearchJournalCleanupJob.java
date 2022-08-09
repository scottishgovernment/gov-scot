package scot.gov.www.searchjournal;

import org.hippoecm.repository.util.DateTools;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.FeatureFlag;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.Calendar;

/**
 * Delete search journal entries older than 30 days.
 */
public class SearchJournalCleanupJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(SearchJournalCleanupJob.class);

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        Session session = context.createSystemSession();
        FeatureFlag enabled = new FeatureFlag(session, "SearchJournalCleanupJob");

        if (!enabled.isEnabled()) {
            return;
        }

        LOG.info("Running SearchJournalCleanupJob");
        NodeIterator nodeIterator = nodesToDelete(session);
        int count = 0;
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            deleteEntry(node);
            intermittentSave(session, count++);
        }
        session.save();
        LOG.info("Finished SearchJournalCleanupJob, {} deleted", count);
    }

    NodeIterator nodesToDelete(Session session) throws RepositoryException {
        Query query = query(session);
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

    Query query(Session session) throws RepositoryException {
        String template = "//element(*, searchjournal:entry)[@searchjournal:timestamp < %s] order by @searchjournal:timestamp";
        String xpath = String.format(template, DateTools.createXPathConstraint(session, cutoff()));
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        query.setLimit(10000);
        return query;
    }

    Calendar cutoff() {
        Calendar cutoff = Calendar.getInstance();
        cutoff.add(Calendar.DATE, -30);
        return cutoff;
    }

}
