package scot.gov.www;

import org.hippoecm.repository.api.HippoNode;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FOINumberDaemonModule extends DaemonModuleBase {

    private static final Logger LOG = LoggerFactory.getLogger(FolderTypesDaemonModule.class);

    public static final String HIPPOSTD_TAGS = "hippostd:tags";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success()
                && event.subjectPath().startsWith("/content/documents/govscot/publications/foi-eir-release/")
                && "govscot:Publication".equals(event.documentType());
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {

        HippoNode foi = (HippoNode) getLatestVariant(session.getNodeByIdentifier(event.subjectId()));
        Node foiFolder = foi.getParent().getParent();
        String [] parts = foiFolder.getName().replace("--", "-").split("-");

        if (parts.length < 3) {
            LOG.warn("Unable to get FOI number for {}", foi.getPath());
            return;
        }

        String foiNumber = parts[2];
        Value[] tags = foi.hasProperty(HIPPOSTD_TAGS)
                ? foi.getProperty(HIPPOSTD_TAGS).getValues()
                : new Value [] {};
        List<Value> tagList = new ArrayList<>(Arrays.asList(tags));
        if (!containsTag(foiNumber, tagList)) {
            tagList.add(session.getValueFactory().createValue(foiNumber));
            foi.setProperty(HIPPOSTD_TAGS, tagList.toArray(new Value[tagList.size()]));
            session.save();
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
