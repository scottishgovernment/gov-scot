package scot.gov.www;

import org.hippoecm.repository.api.HippoNode;
import org.onehippo.repository.events.HippoWorkflowEvent;

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
public class DocumentOrderDaemonModule extends DaemonModuleBase {

    private Map<String, SortOrder> directionMap = new HashMap<>();

    enum SortOrder {
        DESCENDING, ASCENDING;
    }

    @Override
    public void initialize(final Session session) throws RepositoryException {
        super.initialize(session);

        directionMap.put("new-publication-folder", SortOrder.ASCENDING);
        directionMap.put("new-minutes-folder", SortOrder.ASCENDING);
        directionMap.put("new-foi-folder", SortOrder.ASCENDING);
        directionMap.put("new-speech-or-statement-folder", SortOrder.ASCENDING);
        directionMap.put("new-publication-month-folder", SortOrder.DESCENDING);
        directionMap.put("new-publication-year-folder", SortOrder.DESCENDING);
        directionMap.put("new-directorate-folder", SortOrder.ASCENDING);
        directionMap.put("new-policy-folder", SortOrder.ASCENDING);
        directionMap.put("new-news-month-folder", SortOrder.DESCENDING);
        directionMap.put("new-news-year-folder", SortOrder.DESCENDING);
        directionMap.put("new-group", SortOrder.ASCENDING);
        directionMap.put("new-topic", SortOrder.ASCENDING);
        directionMap.put("new-issue", SortOrder.ASCENDING);
        directionMap.put("new-news-document", SortOrder.ASCENDING);
    }

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success();
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {

        Node node = null;
        Node parentfolder = null;
        if ("add".equals(event.action())) {
            node = session.getNode(event.result());
            parentfolder = session.getNode(event.subjectPath());
        }

        if ("rename".equals(event.action())) {
            node = session.getNode(event.result());
            parentfolder = session.getNodeByIdentifier((event.subjectId()));
        }

        if ("setDisplayName".equals(event.action())) {
            node = session.getNode(event.subjectPath());
            parentfolder = session.getNode(event.subjectPath()).getParent();
        }

        if (parentfolder == null) {
            return;
        }

        HippoNode parentFolder = getParentFolder(event);
        if (parentFolder == null) {
            return;
        }

        SortOrder sortOrder = determineSortOrder(parentFolder);
        if (sortOrder == null) {
            // no sort order is specified so do not take any action
            // for example this is the case for the pages or documents folders of publications.
            return;
        }

        sortChildren(parentFolder, node, sortOrder);

    }

    HippoNode getParentFolder(HippoWorkflowEvent event) throws RepositoryException {
        if ("add".equals(event.action())) {
            return (HippoNode) session.getNode(event.subjectPath());
        }

        if ("rename".equals(event.action())) {
            Node node = session.getNodeByIdentifier((event.subjectId()));
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

    public void sortChildren(Node folder, Node node, SortOrder sortOrder) throws RepositoryException {

        List<String> sortedNames = sortedNames(folder.getNodes());
        if (sortOrder == SortOrder.DESCENDING) {
            Collections.reverse(sortedNames);
        }

        int index = sortedNames.indexOf(node.getName());
        if (sortOrder == SortOrder.ASCENDING && index == sortedNames.size() - 1) {
            return;
        }

        if (sortOrder == SortOrder.DESCENDING && index == 0) {
            return;
        }

        String orderBefore = sortOrder == SortOrder.ASCENDING ?
                sortedNames.get(index + 1) : sortedNames.get(index - 1);
        folder.orderBefore(node.getName(), orderBefore);
        session.save();
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
