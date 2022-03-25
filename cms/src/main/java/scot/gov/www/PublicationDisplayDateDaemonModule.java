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
        if (subject.isNodeType("govscot:Publication")) {
            publication = subject;
        }

        // some event have the
        if (subject.isNodeType("hippo:handle")) {
            publication = new HippoUtils().getVariant(subject.getNodes());
        }

        if (publication == null) {
            return;
        }

        maintainDisplayDate(publication);
    }

    void maintainDisplayDate(Node publication) throws RepositoryException {
        Calendar newDisplayDate = getDisplayDate(publication);

        // only do a save if the latest date has actually changed
        if (shouldUpdate(publication, newDisplayDate)) {
            setDisplayDate(publication, newDisplayDate);
            return;
        }
    }

    boolean shouldUpdate(Node publication, Calendar newDisplayDate) throws RepositoryException {
        if (!publication.hasProperty(DISPLAY_DATE)) {
            return true;
        }
        Calendar currentDisplayDate = publication.getProperty(DISPLAY_DATE).getDate();
        return !newDisplayDate.equals(currentDisplayDate);
    }

    void setDisplayDate(Node publication, Calendar date) throws RepositoryException {
        new HippoUtils().apply(
                publication.getParent().getNodes(publication.getName()),
                pub -> pub.setProperty(DISPLAY_DATE, date)
        );

        session.save();
    }

    Calendar getDisplayDate( Node publication) throws RepositoryException {

        return publication.hasNode(UPDATE_HISTORY)
                ? getMostRecentDateFromUpdateHistory(publication)
                : first(publication,
                        "govscot:publicationDate", "hippostdpubwf:publicationDate", "hippostdpubwf:lastModificationDate");
    }

    Calendar first(Node node, String ...props) throws RepositoryException {
        for (String prop : props) {
            if (useProperty(node, prop)) {
                return node.getProperty(prop).getDate();
            }
        }
        return null;
    }

    boolean useProperty(Node node, String prop) throws RepositoryException {
        // 'empty' dates are set to year 1, we want to treat these as null / empty
        return node.hasProperty(prop) && node.getProperty(prop).getDate().get(Calendar.YEAR) != 1;
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
        return lastUpdated(newUpdate).after(lastUpdated(currentUpdate));
    }

    Calendar lastUpdated(Node node) throws RepositoryException {
        return node.getProperty("govscot:lastUpdated").getDate();
    }
}
