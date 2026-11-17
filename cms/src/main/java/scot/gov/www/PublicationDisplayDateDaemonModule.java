package scot.gov.www;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.repository.events.HippoWorkflowEvent;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.Calendar;

/**
 * When a publication is published, or an author requests publication, ensure that the displayDate field is populated
 * from the most recent entry in the updateHistory.
 *
 * When an editor accepts a publication request the document workflow posts a "publish" event part way through the
 * acceptRequest call, before the published variant has been saved.  Since this listener runs asynchronously it would
 * race with that save, so instead we set the date when the request is made and ignore the publish event logged
 * during the accept.  The accept then copies the date from the unpublished variant into the published one.
 */
public class PublicationDisplayDateDaemonModule extends DaemonModuleBase {

    private static final String PREFIX = "/content/documents/govscot/publications/";

    private static final String UPDATE_HISTORY = "govscot:updateHistory";

    private static final String DISPLAY_DATE = "govscot:displayDate";

    private static final String PUBLISH = "publish";

    private static final String REQUEST_PUBLICATION = "requestPublication";

    private static final String ACCEPT_REQUEST_INTERACTION_SUFFIX = ":acceptRequest";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success()
                && StringUtils.startsWith(event.subjectPath(), PREFIX)
                && (isPublish(event) || isRequestPublication(event));
    }

    boolean isPublish(HippoWorkflowEvent event) {
        // the publish event logged during acceptRequest is posted before the publish has been saved, the date
        // will already have been set when the publication was requested
        return StringUtils.equals(event.action(), PUBLISH)
                && !StringUtils.startsWith(event.interaction(), "embargo:")
                && !StringUtils.endsWith(event.interaction(), ACCEPT_REQUEST_INTERACTION_SUFFIX);
    }

    boolean isRequestPublication(HippoWorkflowEvent event) {
        return StringUtils.equals(event.action(), REQUEST_PUBLICATION);
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        Node subject = session.getNodeByIdentifier(event.subjectId());
        Node handle = subject.isNodeType("hippo:handle") ? subject : subject.getParent();
        if (!handle.isNodeType("hippo:handle")) {
            return;
        }

        // use the unpublished variant since it holds the latest content: when a publication is requested the
        // published variant (if any) is still the previous version
        HippoUtils hippoUtils = new HippoUtils();
        Node publication = hippoUtils.getUnpublishedVariant(handle);
        if (publication == null) {
            publication = hippoUtils.getVariant(handle.getNodes(handle.getName()));
        }
        if (publication != null && !isPublication(publication)) {
            publication = null;
        }

        if (publication == null) {
            return;
        }

        maintainDisplayDate(publication);
    }

    boolean isPublication(Node node) throws RepositoryException {
        return node.isNodeType("govscot:Publication");
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
