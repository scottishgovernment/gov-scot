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

    private Map<String, SortOrder> directionMap = new HashMap<>();

    enum SortOrder {
        DESCENDING, ASCENDING;
    }

    @Override
    public void initialize(final Session session) throws RepositoryException {
        this.session = session;
        HippoServiceRegistry.registerService(this, HippoEventBus.class);

        directionMap.put("new-publication-folder", SortOrder.ASCENDING);
        directionMap.put("new-minutes-folder", SortOrder.ASCENDING);
        directionMap.put("new-foi-folder", SortOrder.ASCENDING);
        directionMap.put("new-speech-or-statement-folder", SortOrder.ASCENDING);
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

        try {
            HippoNode parentFolder = getParentFolder(event);
            if (parentFolder == null) {
                return;
            }

            SortOrder sortOrder = determineSortOrder(parentFolder);

            if (sortOrder == null) {
                // no sort order is specified so do not take any action

                LOG.info("No sort order!");
                return;
            }

            sortChildren(parentFolder, sortOrder);
            session.save();
        } catch (RepositoryException e) {
            LOG.error("Unexpected exception while doing simple JCR read operations", e);
        }
    }

    HippoNode getParentFolder(HippoWorkflowEvent event) throws RepositoryException {
        if ("add".equals(event.action())) {
            return (HippoNode) session.getNode(event.subjectPath());
        }

        if ("rename".equals(event.action())) {
            Node node = session.getNode(event.subjectPath());
            return (HippoNode) node;
        }

        if ("setDisplayName".equals(event.action())) {
            return (HippoNode) session.getNodeByIdentifier(event.subjectId()).getParent();
        }

        return null;
    }

    SortOrder determineSortOrder(Node folder) throws RepositoryException {
        if (!folder.hasProperty("hippostd:foldertype")) {
            return null;
        }

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

        others.sort(compareNodeNames(nameMap));
        folders.sort(compareNodeNames(nameMap));

        List<String> names = new ArrayList<>();
        names.addAll(others);
        names.addAll(folders);
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
