package scot.gov.www;

import org.hippoecm.repository.api.HippoNode;
import org.onehippo.repository.events.HippoWorkflowEvent;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Event listener to set the available actions available to new publication month folders depending on the type
 * of publication.
 */
public class FolderTypesDaemonModule extends DaemonModuleBase {

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success() && "add".equals(event.action());
    }

    public void doHandleEvent(final HippoWorkflowEvent event) throws RepositoryException {
        HippoNode newFolder = (HippoNode) session.getNode(event.result());
        if (!FolderUtils.hasFolderType(newFolder, "new-publication-folder")) {
            return;
        }

        // if the type is minutes, speech / statement or FOI then alter the folder type
        Node typeFolder = newFolder.getParent().getParent();
        if ("minutes".equals(typeFolder.getName())) {
            setFolderType(newFolder, "new-minutes-folder");
        }

        if ("speech-statement".equals(typeFolder.getName())) {
            setFolderType(newFolder, "new-speech-or-statement-folder");
        }

        if ("foi-eir-release".equals(typeFolder.getName())) {
            setFolderType(newFolder, "new-foi-folder");
        }
    }

    private void setFolderType(Node node, String type) throws RepositoryException {
        node.setProperty("hippostd:foldertype", new String []{ type });
        session.save();
    }

}
