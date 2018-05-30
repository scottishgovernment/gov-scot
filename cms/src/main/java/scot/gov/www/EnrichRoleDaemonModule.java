package scot.gov.www;

import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.api.HippoNode;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class EnrichRoleDaemonModule implements DaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(EnrichRoleDaemonModule.class);

    public static final String PUBLICATION_INTERACTION = "default:handle:publish";

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
        if (event.success() && PUBLICATION_INTERACTION.equals(event.interaction())) {
            postPublish(event);
        }
    }

    private void postPublish(final HippoWorkflowEvent workflowEvent) {
        try {
            final HippoNode handle = (HippoNode) session.getNodeByIdentifier(workflowEvent.subjectId());
            final Node published = getPublishedVariant(handle);

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

            // add information from the incumbent to the role
            String incumbentUUID = published.getNode("govscot:incumbent").getProperty("hippo:docbase").getString();
            Node incumbent = getPublishedVariant(session.getNodeByIdentifier(incumbentUUID));
            String incumbentInformation = String.format("%s", incumbent.getProperty("govscot:title").getString());
            published.setProperty("govscot:incumbentInformation", incumbentInformation);
            session.save();
        } catch (ItemNotFoundException e) {
            LOG.warn("Unable to find published item {}", workflowEvent.subjectId(), e);
        } catch (RepositoryException e) {
            LOG.error("Unexpected exception while doing simple JCR read operations", e);
        }
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