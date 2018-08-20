package gov.scot.www;

import org.hippoecm.repository.api.HippoNode;
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

/**
 * Event listener to set the publication type fiedl depending on the folder.
 */
public class PublicationTypeDaemonModule implements DaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(FolderTypesDaemonModule.class);

    private Session session;

    @Override
    public void initialize(final Session session) throws RepositoryException {
        this.session = session;
        HippoServiceRegistry.registerService(this, HippoEventBus.class);
    }

    @Override
    public void shutdown() {
        HippoServiceRegistry.unregisterService(this, HippoEventBus.class);
    }

    @Subscribe
    public void handleEvent(final HippoWorkflowEvent event) {
        if (!event.success()) {
            return;
        }

        if (!event.subjectPath().startsWith("/content/documents/govscot/publications/")) {
            return;
        }

        if (!"govscot:Publication".equals(event.documentType())) {
            return;
        }

        try {
            HippoNode publication = (HippoNode) getLatestVariant(session.getNodeByIdentifier(event.subjectId()));
            Node typeFolder = publication.getParent().getParent().getParent().getParent().getParent();
            publication.setProperty("govscot:publicationType", typeFolder.getName());
            session.save();
        } catch (RepositoryException e) {
            LOG.error("Unexpected exception while doing simple JCR read operations", e);
        }
    }

    private static Node getLatestVariant(Node handle) throws RepositoryException {
        NodeIterator it = handle.getNodes();
        Node variant = null;
        while (it.hasNext()){
            variant = it.nextNode();
        }
        return variant;
    }
}