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
        directionMap.put("new-publication-month-folder", SortOrder.DESCENDING);
        directionMap.put("new-publication-year-folder", SortOrder.DESCENDING);
        directionMap.put("new-news-month-folder", SortOrder.DESCENDING);
        directionMap.put("new-news-year-folder", SortOrder.DESCENDING);
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

        if (sortOrder == SortOrder.ASCENDING) {
            reorderAscending(parentFolder, node);
        } else{
            reorderDescending(parentFolder, node);
        }
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

    public void reorderAscending(Node folder, Node newNode) throws RepositoryException {
        NodeIterator it = folder.getNodes();
        while (it.hasNext()) {
            Node current = it.nextNode();
            if (newNode.getName().compareTo(current.getName()) < 0) {
                folder.orderBefore(newNode.getName(), current.getName());
                session.save();
                return;
            }
        }
    }

    public void reorderDescending(Node folder, Node newNode) throws RepositoryException {
        NodeIterator it = folder.getNodes();
        while (it.hasNext()) {
            Node current = it.nextNode();
            if (newNode.getName().compareTo(current.getName()) > 0) {
                folder.orderBefore(newNode.getName(), current.getName());
                session.save();
                return;
            }
        }
    }

}
