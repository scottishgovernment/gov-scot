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
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FOINumberDaemonModule implements DaemonModule {

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

        if (!event.subjectPath().startsWith("/content/documents/govscot/publications/foi-eir-release/")) {
            return;
        }

        if (!"govscot:Publication".equals(event.documentType())) {
            return;
        }

        try {
            HippoNode foi = (HippoNode) getLatestVariant(session.getNodeByIdentifier(event.subjectId()));
            Node foiFolder = foi.getParent().getParent();
            String [] parts = foiFolder.getName().replace("--", "-").split("-");

            if (parts.length < 3) {
                LOG.warn("Unable to get FOI number for {}", foi.getPath());
                return;
            }

            String foiNumber = parts[2];
            Value[] tags = foi.hasProperty("hippostd:tags")
                    ? foi.getProperty("hippostd:tags").getValues()
                    : new Value [] {};
            List<Value> tagList = new ArrayList<>(Arrays.asList(tags));
            if (!containsTag(foiNumber, tagList)) {
                tagList.add(session.getValueFactory().createValue(foiNumber));
                foi.setProperty("hippostd:tags", tagList.toArray(new Value [tagList.size()]));
                session.save();
            }

        } catch (RepositoryException e) {
            LOG.error("Unexpected exception while trying to set foi tag", e);
        }
    }

    private boolean containsTag(String tag, List<Value> tags) throws RepositoryException {
        for (Value tagVal : tags) {
            if (tagVal.getString().equals(tag)) {
                return true;
            }
        }
        return false;
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