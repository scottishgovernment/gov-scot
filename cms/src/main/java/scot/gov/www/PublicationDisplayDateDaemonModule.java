package scot.gov.www;

import org.apache.commons.lang.StringUtils;
import org.onehippo.repository.events.HippoWorkflowEvent;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.Calendar;

/**
 * When a publication is publish, ensure the the latestUpdateDate field is populated from the most recent entry in the
 * updateHistory.
 */
public class PublicationDisplayDateDaemonModule extends DaemonModuleBase {

    private static final String PREFIX = "/content/documents/govscot/publications/";

    private static final String UPDATE_HISTORY = "govscot:updateHistory";

    private static final String DISPLAY_DATE = "govscot:displayDate";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success()
                && event.subjectPath().startsWith(PREFIX)
                && StringUtils.equals(event.action(), "publish")
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

        maintainDisplayDate(publication);
    }

    void maintainDisplayDate(Node publication) throws RepositoryException {

        Calendar currentDisplayDate = publication.getProperty(DISPLAY_DATE).getDate();
        Calendar newDisplayDate = getDisplayDate(publication);

        // only do a save if the latest dat ahas actually changed
        if (!newDisplayDate.equals(currentDisplayDate)) {
            setDisplayDate(publication, newDisplayDate);
            return;
        }
    }

    void setDisplayDate(Node publication, Calendar date) throws RepositoryException {
        new HippoUtils().apply(
                publication.getParent().getNodes(publication.getName()),
                pub -> pub.setProperty(DISPLAY_DATE, date)
        );
        session.save();
    }

    Calendar getDisplayDate(Node publication) throws RepositoryException {
        return publication.hasProperty(UPDATE_HISTORY)
                ? getMostRecentDateFromUpdateHistory(publication)
                : first(publication,
                        "govscot:publicationDate", "hippostdpubwf:publicationDate", "hippostdpubwf:lastModificationDate");
    }

    Calendar first(Node node, String ...props) throws RepositoryException {
        for (String prop : props) {
            if (node.hasProperty(prop)) {
                return node.getProperty(prop).getDate();
            }
        }
        return null;
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
