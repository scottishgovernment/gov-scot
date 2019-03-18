package scot.gov.www;

import org.hippoecm.repository.api.HippoNode;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.*;

/**
 * Maintain documents in a sorted order as they are added or renamed.  This applies to the following folders:
 *
 * policy folders, alpha ascending
 * publications:
 *  - year, alpha descending
 *  - months, alpha descending
 *  - publications, alpha ascending
 * directorates alpha ascending
 */
public class DocumentOrderDaemonModule implements DaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentOrderDaemonModule.class);

    private Session session;

    private Set<String> sortActions = new HashSet<>();

    private Map<String, SortOrder> directionMap = new HashMap<>();

    enum SortOrder {
        DESCENDING, ASCENDING;
    }

    @Override
    public void initialize(final Session session) throws RepositoryException {
        this.session = session;
        HippoServiceRegistry.registerService(this, HippoEventBus.class);

        Collections.addAll(sortActions, "add", "setDisplayName");

        directionMap.put("new-publication-folder", SortOrder.ASCENDING);
        directionMap.put("new-publication-month-folder", SortOrder.DESCENDING);
        directionMap.put("new-publication-year-folder", SortOrder.DESCENDING);
        directionMap.put("new-directorate-folder", SortOrder.ASCENDING);
        directionMap.put("new-policy-folder", SortOrder.ASCENDING);
        directionMap.put("new-group", SortOrder.ASCENDING);
        directionMap.put("new-topic", SortOrder.ASCENDING);
        directionMap.put("new-issue", SortOrder.ASCENDING);
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
        if (!shouldSortFolder(event)) {
            return;
        }

        try {
            HippoNode newItem = (HippoNode) session.getNode(event.subjectPath());
            SortOrder sortOrder = determineSortOrder(newItem.getParent());
            if (sortOrder == null) {
                // no sort order is specified so do not take any action
                return;
            }

            sortChildren(newItem.getParent(), sortOrder);
            session.save();
        } catch (RepositoryException e) {
            LOG.error("Unexpected exception while doing simple JCR read operations", e);
        }
    }

    boolean shouldSortFolder(HippoWorkflowEvent event) {
        return sortActions.contains(event.action());
    }

    SortOrder determineSortOrder(Node folder) throws RepositoryException {
        for (Value value : folder.getProperty("hippostd:foldertype").getValues()) {
            if (directionMap.containsKey(value.getString())) {
                return directionMap.get(value.getString());
            }
        }
        return null;
    }

    public void sortChildren(Node node, SortOrder sortOrder) throws RepositoryException {
        List<String> sortedNames = sortedNames(node.getNodes());
        if (sortOrder == SortOrder.DESCENDING) {
            Collections.reverse(sortedNames);
        }

        for (int i = sortedNames.size() - 1; i >= 0; i--) {
            String before = sortedNames.get(i);
            String after = i < sortedNames.size() - 1 ? sortedNames.get(i + 1) : null;
            node.orderBefore(before, after);
        }
    }

    /**
     * Sort the nodes in an iterator, Folders in alphabetical order first then other documents in alphabetical order.
     */
    List<String> sortedNames(NodeIterator it) throws RepositoryException {

        // for each node work out what name we want to sort by and
        // partition them into folders and 'others'
        Map<String, String> nameMap = new HashMap<>();
        List<String> folders = new ArrayList<>();
        List<String> others = new ArrayList<>();
        apply(it, node -> {
            nameMap.put(node.getName(), name(node));
            if (isHippoFolder(node)) {
                folders.add(node.getName());
            } else {
                others.add(node.getName());
            }
        });

        folders.sort(compareNodeNames(nameMap));
        others.sort(compareNodeNames(nameMap));

        List<String> names = new ArrayList<>();
        names.addAll(folders);
        names.addAll(others);
        return names;
    }

    String name(Node node) throws RepositoryException {
        return node.hasProperty("hippo:name") ? node.getProperty("hippo:name").getString() : node.getName();
    }

    Comparator<String> compareNodeNames(Map<String, String> nameMap) {
        return (l, r) -> String.CASE_INSENSITIVE_ORDER.compare(nameMap.get(l), nameMap.get(r));
    }

    void apply(NodeIterator it, ThrowingConsumer consumer) throws RepositoryException {
        while (it.hasNext()) {
            consumer.accept(it.nextNode());
        }
    }

    @FunctionalInterface
    public interface ThrowingConsumer {
        void accept(Node t) throws RepositoryException;
    }

    boolean isHippoFolder(Node node) throws RepositoryException {
        return "hippostd:folder".equals(node.getPrimaryNodeType().getName());
    }
}