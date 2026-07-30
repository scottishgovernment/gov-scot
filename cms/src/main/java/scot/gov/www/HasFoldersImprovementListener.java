package scot.gov.www;

import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.repository.events.HippoWorkflowEvent;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

/**
 * The 'hippostd:hasfolders' property is usually set by the default folder workflow implementation.
 * This property is used to improve UI performance in brXM. The default implementation however, causes
 * issues with nested folders created via template queries.
 *
 * This results in our main content folders mistakenly having the property set to false, causing them
 * to appear like their folders cannot be expanded or collapsed in the CMS.
 *
 * This event listener sets this property where appropriate when folders are created in the CMS.
 */

public class HasFoldersImprovementListener extends DaemonModuleBase {

    private static final String FOLDER_WORKFLOW_CLASS_NAME = "org.hippoecm.repository.standardworkflow.FolderWorkflowImpl";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success()
                && "add".equals(event.action())
                && FOLDER_WORKFLOW_CLASS_NAME.equals(event.className());
    }

    public void doHandleEvent(final HippoWorkflowEvent event) throws RepositoryException {
        final String result = event.result();
        if (result == null || !session.nodeExists(result)) {
            return;
        }

        final Node node = session.getNode(result);
        if (hasFolders(node)) {
            final String propertyName = "hippostd:hasfolders";
            if (!JcrUtils.getBooleanProperty(node, propertyName, false)) {
                node.setProperty(propertyName, true);
                session.save();
            }
        }
    }

    private boolean hasFolders(final Node node) throws RepositoryException {
        final NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            final Node next = nodes.nextNode();
            if (next.isNodeType("hippostd:folder")) {
                return true;
            }
        }
        return false;
    }

}
