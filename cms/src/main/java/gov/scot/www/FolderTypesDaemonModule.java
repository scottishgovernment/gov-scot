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
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

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

        // we only want tyo listen to folder being added
        if (!"threepane:folder-permissions:add".equals(event.interaction())) {
            return;
        }

        try {
            HippoNode newFolder = (HippoNode) session.getNode(event.result());

            if (!isPublicationsMonthFolder(newFolder)) {
                return;
            }

            // if the type is minutes or speech / statement then alter the folder type
            Node typeFolder = newFolder.getParent().getParent();
            switch (typeFolder.getName()) {
                case "minutes" :
                    setFolderType(newFolder, "new-minutes-folder");
                    break;
                case "speech---statement" :
                    setFolderType(newFolder, "new-speech-or-statement-folder");
                    break;
            }
            session.save();
        } catch (RepositoryException e) {
            LOG.error("Unexpected exception while doing simple JCR read operations", e);
        }
    }

    private boolean isPublicationsMonthFolder(HippoNode subject) throws RepositoryException {
        if (!subject.isNodeType("hippostd:folder")) {
            return false;
        }

        // if a month folder is created then alter the foldertype if if is either minutes or speech / statement
        if (!isFolderType(subject, "new-publication-folder")) {
            return false;
        }
        return true;
    }


    private void setFolderType(Node node, String type) throws RepositoryException {
        node.setProperty("hippostd:foldertype", new String []{ type });
    }

    private boolean isFolderType(Node node, String type) throws RepositoryException {
        Property prop = node.getProperty("hippostd:foldertype");
        Value[] values = prop.getValues();
        for (Value v : values) {
            String val = v.getString();
            if (val.equals(type)) {
                return true;
            }
        }
        return false;
    }

}