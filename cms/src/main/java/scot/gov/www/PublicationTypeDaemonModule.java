package scot.gov.www;

import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

/**
 * Event listener to set the publication type field depending on the folder.
 */
public class PublicationTypeDaemonModule implements DaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationTypeDaemonModule.class);

    private static final String PUBLICATION_TYPE_PROPERTY = "govscot:publicationType";

    private static final String PREFIX = "/content/documents/govscot/publications/";

    private static final Set<String> TYPES = unmodifiableSet(new HashSet<>(asList(
            "govscot:Publication",
            "govscot:ComplexDocument",
            "govscot:Minutes",
            "govscot:SpeechOrStatement"
    )));

    private Session session;

    @Override
    public void initialize(Session session) throws RepositoryException {
        this.session = session;
        HippoServiceRegistry.registerService(this, HippoEventBus.class);
    }

    @Override
    public void shutdown() {
        HippoServiceRegistry.unregisterService(this, HippoEventBus.class);
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        if (!event.success()) {
            return;
        }


        if (!event.subjectPath().startsWith(PREFIX)) {
            return;
        }

        if (event.interaction().startsWith("embargo:")) {
            return;
        }

        try {
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

            String typeName = typeName(publication);
            if (!hasPublicationType(publication, typeName)) {
                publication.setProperty(PUBLICATION_TYPE_PROPERTY, typeName);
                session.save();
            }
        } catch (RepositoryException ex) {
            LOG.error("Could not verify publication type for {}",
                    event.subjectPath(),
                    ex);
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
