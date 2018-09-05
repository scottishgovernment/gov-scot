package scot.gov.www;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

        try {
            Node handle = null;
            // a new folder is being added
            if ("threepane:folder-permissions:add".equals(event.interaction())) {
                handle = session.getNode(event.returnValue()).getNode("index");
            }

            // a publication is being edited
            Set<String> publicationtypes = new HashSet<>();
            Collections.addAll(publicationtypes, "govscot:Publication", "govscot:ComplexDocument", "govscot:Minutes", "govscot:SpeechOrStatement");
            if (publicationtypes.contains(event.documentType())) {
                handle = session.getNodeByIdentifier(event.subjectId());
            }

            HippoNode publication = (HippoNode) getLatestVariant(handle);
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