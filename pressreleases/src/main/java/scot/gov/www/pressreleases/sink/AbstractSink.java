package scot.gov.www.pressreleases.sink;

import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.pressreleases.domain.PressRelease;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import static org.apache.commons.lang.StringUtils.isBlank;

public abstract class AbstractSink implements PressReleaseSink {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSink.class);

    @Override
    public abstract void acceptPressRelease(PressRelease pressrelease) throws RepositoryException;

    @Override
    public abstract void removeDeletedPressRelease(String id) throws RepositoryException;

    String delete(String id, String type, Session session) throws RepositoryException {
        String xpath = String.format("//element(*, %s)[@govscot:externalId = '%s']", type, id);
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        QueryResult result = query.execute();
        if (!result.getNodes().hasNext()) {
            return null;
        }
        Node node = result.getNodes().nextNode();
        String path = node.getParent().getPath();
        node.getParent().remove();
        session.save();
        return path;
    }

    String update(ContentNode contentNode, String location, Session session) {
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
        String updatedDocumentLocation = importTask.createOrUpdateDocumentFromVariantContentNode(
                contentNode, contentNode.getPrimaryType(), location,"en", contentNode.getName());
        try {
            documentManager.publishDocument(updatedDocumentLocation);
        } catch (IllegalStateException e) {
            LOG.warn("No publish action for " + location + " no changes were found", e);
        }
        return updatedDocumentLocation;
    }

    void setFolderType(Node node, String type) throws RepositoryException {
        node.setProperty("hippostd:foldertype", new String [] { type });
    }

    void detectMissingSlug(Session session, String location, PressRelease release) throws RepositoryException {
        Node handle = session.getNode(location);
        NodeIterator nodeIterator = handle.getNodes(handle.getName());
        while (nodeIterator.hasNext()) {
            Node variant = nodeIterator.nextNode();
            if (!variant.hasProperty("govscot:slug") || isBlank(variant.getProperty("govscot:slug").getString())) {
                LOG.warn("News item missing slug {}, {}", handle.getPath(), release);
            }
        }
    }
}
