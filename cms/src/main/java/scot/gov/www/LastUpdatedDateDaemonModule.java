package scot.gov.www;

import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.Calendar;

/**
 * When a publication is publish, ensure the the latestUpdateDate field is populated from the most recent entry in the
 * updateHistory.
 */
public class LastUpdatedDateDaemonModule extends DaemonModuleBase {

    private static final Logger LOG = LoggerFactory.getLogger(LastUpdatedDateDaemonModule.class);

    private static final String PREFIX = "/content/documents/govscot/publications/";

    private static final String UPDATE_HISTORY = "govscot:updateHistory";

    private static final String LATEST_UPDATE_DATE = "govscot:latestUpdateDate";
    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success()
                && event.subjectPath().startsWith(PREFIX)
                && event.action().equals("publish")
                && !event.interaction().startsWith("embargo:");
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        Node subject = session.getNodeByIdentifier(event.subjectId());

        Node publication = null;
        if (subject.isNodeType("govscot:Publication") || subject.isNodeType("govscot:ComplexDocument")) {
            publication = subject;
        }

        // some event have the
        if (subject.isNodeType("hippo:handle")) {
            publication = getLatestVariant(subject);
        }

        if (publication == null) {
            return;
        }

        // update the last modified date if there are any
        if (!publication.hasNode(UPDATE_HISTORY)) {
            return;
        }

        maintainLastUpdatedDate(publication);
    }

    void maintainLastUpdatedDate(Node publication) throws RepositoryException {

        Calendar mostRecentUpdateDate = getMostRecentDateFromUpdateHistory(publication);

        if (!publication.hasProperty(LATEST_UPDATE_DATE)) {
            setLatestUpdateDate(publication, mostRecentUpdateDate);
            return;
        }

        Calendar lastUpdated = publication.getProperty("govscot:latestUpdateDate").getDate();

        // only do a save if the latest dat ahas actually changed
        if (!mostRecentUpdateDate.equals(lastUpdated)) {
            setLatestUpdateDate(publication, mostRecentUpdateDate);
            return;
        }
    }

    void setLatestUpdateDate(Node publication, Calendar date) throws RepositoryException {
        new HippoUtils().apply(
                publication.getParent().getNodes(publication.getName()),
                pub -> pub.setProperty(LATEST_UPDATE_DATE, date)
        );
        session.save();
    }

    Calendar getMostRecentDateFromUpdateHistory(Node publication) throws RepositoryException {
        NodeIterator it = publication.getNodes(UPDATE_HISTORY);
        Node mostRecent = it.nextNode();
        while (it.hasNext()) {
            Node next = it.nextNode();
            if (isAfter(mostRecent, next)) {
                mostRecent = next;
            }
        }
        return lastUpdated(mostRecent);
    }

    boolean isAfter(Node currentUpdate, Node newUpdate) throws RepositoryException {
        return lastUpdated(newUpdate)
                .after(lastUpdated(currentUpdate));
    }

    Calendar lastUpdated(Node node) throws RepositoryException {
        return node.getProperty("govscot:lastUpdated").getDate();
    }

    private static Node getLatestVariant(Node handle) throws RepositoryException {
        NodeIterator it = handle.getNodes();
        Node variant = null;
        while (it.hasNext()) {
            Node next = it.nextNode();
            if (!next.isNodeType("hippostdpubwf:request")) {
                variant = next;
            }
        }
        return variant;
    }

}
