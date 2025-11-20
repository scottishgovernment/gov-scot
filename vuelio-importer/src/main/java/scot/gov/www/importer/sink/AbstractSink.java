package scot.gov.www.importer.sink;

import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.importer.domain.PressRelease;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import static org.apache.commons.lang.StringUtils.isBlank;

public abstract class AbstractSink implements ContentSink {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSink.class);

    @Override
    public abstract void acceptPressRelease(PressRelease pressRelease) throws RepositoryException;

    @Override
    public abstract void removeDeletedPressRelease(String id) throws RepositoryException;

    String depublish(String id, Session session) throws RepositoryException {
        String xpath = String.format("//element(*)[@govscot:externalId = '%s']", id);
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        QueryResult result = query.execute();
        if (!result.getNodes().hasNext()) {
            return null;
        }
        Node node = result.getNodes().nextNode();
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        try {
            documentManager.depublishDocument(node.getParent().getPath());
            session.save();
        } catch (IllegalStateException e) {
            LOG.warn("No depublish action for {}", node.getPath() , e);
        }
        return node.getPath();
    }

    String update(ContentNode contentNode, String location, Session session) {
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
        String updatedDocumentLocation = importTask.createOrUpdateDocumentFromVariantContentNode(
                contentNode, contentNode.getPrimaryType(), location,"en", contentNode.getName());
        try {
            documentManager.publishDocument(updatedDocumentLocation);
        } catch (IllegalStateException e) {
            LOG.warn("No publish action for {}", location, e);
        }
        return updatedDocumentLocation;
    }

    void setFolderType(Node node, String type) throws RepositoryException {
        node.setProperty("hippostd:foldertype", new String [] { type });
    }

    void detectMissingSlug(Session session, String location, PressRelease pressRelease) throws RepositoryException {
        Node handle = session.getNode(location);
        NodeIterator nodeIterator = handle.getNodes(handle.getName());
        while (nodeIterator.hasNext()) {
            Node variant = nodeIterator.nextNode();
            if (!variant.hasProperty("govscot:slug") || isBlank(variant.getProperty("govscot:slug").getString())) {
                LOG.warn("News item missing slug {}, {}", handle.getPath(), pressRelease);
            }
        }
    }
}
