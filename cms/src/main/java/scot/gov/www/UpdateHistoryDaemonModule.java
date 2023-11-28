package scot.gov.www;

import org.onehippo.repository.events.HippoWorkflowEvent;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.*;

public class UpdateHistoryDaemonModule extends DaemonModuleBase {

    private static final String UPDATE_HISTORY = "govscot:updateHistory";

    private static final String OBTAIN_INSTANCE = "default:handle:obtainEditableInstance";

    private static final String FLAG = "revisionHistoryFlag";

    private HippoUtils hippoUtils = new HippoUtils();

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return OBTAIN_INSTANCE.equals(event.interaction());
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {

        Node handle = session.getNodeByIdentifier(event.subjectId());
        Node draft = hippoUtils.find(handle.getNodes(handle.getName()), n -> "draft".equals(n.getProperty("hippostd:state").getString()));

        if (draft == null) {
            return;
        }

        // this should only happen the first time this item is opened
        if (draft.hasProperty(FLAG)) {
            return;
        }

        // if there is an update history then reverse it
        if (draft.hasNode(UPDATE_HISTORY)) {
            reverseUpdateHistory(draft);
            draft.setProperty(FLAG, true);
            session.save();
        }
    }

    void reverseUpdateHistory(Node draft) throws RepositoryException {
        List<Item> items = new ArrayList<>();
        hippoUtils.apply(draft.getNodes(UPDATE_HISTORY), n -> {
            Item item = new Item();
            item.lastUpdated = n.getProperty("govscot:lastUpdated").getDate();
            item.updateText = n.getProperty("govscot:updateText").getString();
            items.add(item);
            n.remove();
        });
        Collections.reverse(items);
        for (Item item : items) {
            Node historyiten = draft.addNode(UPDATE_HISTORY, "govscot:UpdateHistory");
            historyiten.setProperty("govscot:lastUpdated", item.lastUpdated);
            historyiten.setProperty("govscot:updateText", item.updateText);
        }
    }

    class Item {
        Calendar lastUpdated;
        String updateText;
    }
}