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
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Event listener to set the available actions available to new publication month folders depending on the type
 * of publication.
 */
public class FolderTypesDaemonModule implements DaemonModule {

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

        // we only want to listen to folder being added
        if (!"add".equals(event.action())) {
            return;
        }

        try {
            HippoNode newFolder = (HippoNode) session.getNode(event.result());
            if (!FolderUtils.hasFolderType(newFolder, "new-publication-folder")) {
                return;
            }

            // if the type is minutes, speech / statement or FOI then alter the folder type
            Node typeFolder = newFolder.getParent().getParent();
            if ("minutes".equals(typeFolder.getName())) {
                setFolderType(newFolder, "new-minutes-folder");
            }

            if ("speech---statement".equals(typeFolder.getName())) {
                setFolderType(newFolder, "new-speech-or-statement-folder");
            }

            if ("foi-eir-release".equals(typeFolder.getName())) {
                setFolderType(newFolder, "new-foi-folder");
            }

        } catch (RepositoryException e) {
            LOG.error("Unexpected exception while doing simple JCR read operations", e);
        }
    }

    private void setFolderType(Node node, String type) throws RepositoryException {
        node.setProperty("hippostd:foldertype", new String []{ type });
        session.save();
    }

}