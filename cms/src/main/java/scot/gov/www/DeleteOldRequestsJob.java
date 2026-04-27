package scot.gov.www;

import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

public class DeleteOldRequestsJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteOldRequestsJob.class);

    private static final String XPATH
            = "//element(*, hippostdpubwf:request)[hippostdpubwf:type = 'rejected']";

    private final HippoUtils hippoUtils = new HippoUtils();

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {
        Session session = context.createSystemSession();
        try {

            LOG.info("DeleteOldRequestsJob running");
            hippoUtils.executeXpathQuery(session, XPATH, this::isOld, this::remove);
            session.save();
        } catch (RepositoryException e) {
            LOG.error("exception tyring to clean up old rejected publish requests");
            throw e;
        } finally {
            LOG.info("DeleteOldRequestsJob finished");
            session.logout();
        }
    }

    boolean isOld(Node node) throws RepositoryException {
        LocalDate fourWeeksAgo = LocalDate.now().minusWeeks(4);
        LocalDate createdDate = createdDate(node);
        return createdDate.isBefore(fourWeeksAgo);
    }

    LocalDate createdDate(Node node) throws RepositoryException {
        Calendar createdDate = node.getProperty("hippostdpubwf:creationDate").getDate();
        return LocalDateTime.ofInstant(
                createdDate.toInstant(),
                createdDate.getTimeZone().toZoneId()
        ).toLocalDate();
    }

    void remove(Node node) throws RepositoryException {
        LOG.info("removing old rejected request from {}", node.getParent().getPath());
        node.remove();
    }

}
