package scot.gov.scheduler;

import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.Calendar;

public class PreviewLinksCleanupJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(PreviewLinksCleanupJob.class);

    private static final String CONFIG_BATCH_SIZE = "batchsize";
    private static final String PREVIEW_LINKS_QUERY = "//element(*, staging:preview)";

    @Override
    public void execute(final RepositoryJobExecutionContext context) throws RepositoryException {
        LOG.info("Running preview links cleanup job");
        final Session session = context.createSystemSession();
        try {
            long batchSize;
            try {
                batchSize = Long.parseLong(context.getAttribute(CONFIG_BATCH_SIZE));
            } catch (NumberFormatException e) {
                LOG.warn("Incorrect batch size '"+context.getAttribute(CONFIG_BATCH_SIZE)+"'. Setting default to 100");
                batchSize = 100;
            }
            removeOldFormData(batchSize, session);
        } finally {
            session.logout();
        }
    }

    private void removeOldFormData(final long batchSize, final Session session) throws RepositoryException {
        final QueryManager queryManager = session.getWorkspace().getQueryManager();
        final Query query = queryManager.createQuery(PREVIEW_LINKS_QUERY, Query.XPATH);
        final NodeIterator nodes = query.execute().getNodes();
        int count = 0;

        while (nodes.hasNext()) {
            try {
                final Node node = nodes.nextNode();
                Calendar expirationCalendar = JcrUtils.getDateProperty(node, "staging:expirationdate", null);
                if(expirationCalendar == null || Calendar.getInstance().before(expirationCalendar)){
                    break;
                }
                LOG.debug("Removing preview node at {}", node.getPath());
                node.remove();
                if (count++ % batchSize == 0) {
                    session.save();
                    Thread.sleep(100);
                }
            } catch (RepositoryException | InterruptedException e) {
                LOG.error("Error while cleaning up preview links", e);
            }
        }
        if (session.hasPendingChanges()) {
            session.save();
        }
        if (count > 0) {
            LOG.info("Done cleaning " + count + " items");
        } else {
            LOG.info("No timed out items");
        }
    }

}
