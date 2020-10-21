package scot.gov.www;

import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.api.HippoNode;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.repository.events.HippoWorkflowEvent;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class EnrichRoleDaemonModule extends DaemonModuleBase {

    public static final String PUBLICATION_INTERACTION = "default:handle:publish";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success() && PUBLICATION_INTERACTION.equals(event.interaction());
    }

    public void doHandleEvent(HippoWorkflowEvent workflowEvent) throws RepositoryException {
        HippoNode handle = (HippoNode) session.getNodeByIdentifier(workflowEvent.subjectId());
        Node published = getPublishedVariant(handle);

        // if there is no published variant then return
        if (published == null) {
            return;
        }

        // we are only interested in roles
        if (!"govscot:Role".equals(published.getPrimaryNodeType().getName())) {
            return;
        }

        // if this role has no incumbent then return
        if (!published.hasNode("govscot:incumbent")) {
            return;
        }

        String incumbentUUID = published.getNode("govscot:incumbent").getProperty("hippo:docbase").getString();
        Node incumbent = getPublishedVariant(session.getNodeByIdentifier(incumbentUUID));

        // this role has an incumbent and so we do not want to include that incumbent in search results
        incumbent.setProperty("govscot:excludeFromSearchIndex", true);

        // add information from the incumbent to the role
        String incumbentInformation = String.format("%s", incumbent.getProperty("govscot:title").getString());
        published.setProperty("govscot:incumbentInformation", incumbentInformation);
        session.save();
    }

    private static Node getPublishedVariant(Node handle) throws RepositoryException {
        for (Node variant : new NodeIterable(handle.getNodes(handle.getName()))) {
            final String state = JcrUtils.getStringProperty(variant, HippoStdNodeType.HIPPOSTD_STATE, null);
            if (HippoStdNodeType.PUBLISHED.equals(state)) {
                return variant;
            }
        }
        return null;
    }

}
