package scot.gov.www;

import org.onehippo.repository.events.HippoWorkflowEvent;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

/**
 * Event listener to set the publication type field depending on the folder.
 */
public class PublicationTypeDaemonModule extends DaemonModuleBase {

    private static final String PUBLICATION_TYPE_PROPERTY = "govscot:publicationType";

    private static final String PREFIX = "/content/documents/govscot/publications/";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success() && event.subjectPath().startsWith(PREFIX) && !event.interaction().startsWith("embargo:");
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {

        Node subject = session.getNodeByIdentifier(event.subjectId());

        Node publication = null;
        if (subject.isNodeType("govscot:Publication")) {
            publication = subject;
        }

        // some event have the
        if (subject.isNodeType("hippo:handle")) {
            publication = getLatestVariant(subject);
        }

        if (publication == null) {
            return;
        }

        String typeName = typeName(publication);
        if (!hasPublicationType(publication, typeName)) {
            publication.setProperty(PUBLICATION_TYPE_PROPERTY, typeName);
            session.save();
        }
    }

    private boolean hasPublicationType(Node publication, String typeName) throws RepositoryException {
        return typeName.equals(getStringProperty(publication, PUBLICATION_TYPE_PROPERTY));
    }

    private String typeName(Node node) throws RepositoryException {
        Node current = node;
        Node previous;
        do {
            previous = current;
            current = current.getParent();
        } while (!"publications".equals(current.getName()));
        return previous.getName();
    }

    private String getStringProperty(Node node, String property) throws RepositoryException {
        return node.hasProperty(property) ? node.getProperty(property).getString() : null;
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
