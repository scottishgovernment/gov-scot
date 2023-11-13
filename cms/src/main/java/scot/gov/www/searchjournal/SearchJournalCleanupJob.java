package scot.gov.www.searchjournal;

import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.searchjournal.FeatureFlag;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Delete old search journal entries. The journal is deleted at the mont resolution and at least a months entries will be kept.
 */
public class SearchJournalCleanupJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(SearchJournalCleanupJob.class);

    private static final int MONTH_LIMIT = 24;

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        Session session = context.createSystemSession();
        try {
            FeatureFlag enabled = new FeatureFlag(session, "SearchJournalCleanupJob");
            if (enabled.isEnabled()) {
                doExecute(session);
            }
        } finally {
            session.logout();
        }
    }

    void doExecute(Session session) throws RepositoryException {
        Calendar cutoff = getCutoff();
        for (Node monthNode : getMonthsToDelete(session, cutoff)) {
            LOG.info("removing month {}", monthNode.getPath());
            Node yearNode = monthNode.getParent();
            monthNode.remove();
            if (!yearNode.hasNodes()) {
                LOG.info("removing year {}", yearNode.getPath());
                yearNode.remove();
            }
            session.save();
        }
    }

    List<Node> getMonthsToDelete(Session session, Calendar cutoff) throws RepositoryException {
        Node root = session.getNode("/content/searchjournal");
        List<Node> yearNodes = sortedYears(root);
        List<Node> allMonthNodes = new ArrayList<>();
        for (Node yearNode : yearNodes) {
            allMonthNodes.addAll(sortedMonthsForYear(yearNode, cutoff));
        }
        return allMonthNodes.subList(0, Math.min(MONTH_LIMIT, allMonthNodes.size()));
    }

    List<Node> sortedYears(Node root) throws RepositoryException {
        List<Node> yearNodes = new ArrayList<>();
        NodeIterator yearit = root.getNodes();
        while (yearit.hasNext()) {
            yearNodes.add(yearit.nextNode());
        }
        Collections.sort(yearNodes, this::compareNodeNameAsLong);
        return yearNodes;
    }

    List<Node> sortedMonthsForYear(Node year, Calendar cutoff) throws RepositoryException {
        List<Node> monthNodes = new ArrayList<>();
        NodeIterator monthit = year.getNodes();
        while (monthit.hasNext()) {
            Node month = monthit.nextNode();
            if (includeMonth(month, cutoff)) {
                monthNodes.add(month);
            }
        }
        Collections.sort(monthNodes, this::compareNodeNameAsLong);
        return monthNodes;
    }

    Calendar getCutoff() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(31);
        Date date = Date.from(cutoff.atZone(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    int compareNodeNameAsLong(Node left, Node right) {
        try {
            Long leftValue = Long.parseLong(left.getName());
            Long rightValue = Long.parseLong(right.getName());
            return leftValue.compareTo(rightValue);
        } catch (RepositoryException e) {
            LOG.error("compareNodeNameAsLong failed to compare nodes", e);
            return 0;
        }
    }

    boolean includeMonth(Node monthNode, Calendar cutoff) throws RepositoryException {
        long monthFromNode = Long.parseLong(monthNode.getName());
        long yearFromNode = Long.parseLong(monthNode.getParent().getName());
        if (yearFromNode == cutoff.get(Calendar.YEAR)) {
            return monthFromNode < cutoff.get(Calendar.MONTH);
        }
        return yearFromNode < cutoff.get(Calendar.YEAR);
    }

}
