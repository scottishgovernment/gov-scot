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
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class AbstractSink implements ContentSink {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSink.class);

    private static final String NEWS_PREFIX = "/content/documents/govscot/news/";

    protected final NewsSideEffects newsSideEffects = new NewsSideEffects();

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
        Node handle = node.getParent();
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        try {
            documentManager.depublishDocument(handle.getPath());
            if (handle.getPath().startsWith(NEWS_PREFIX)) {
                newsSideEffects.onDepublish(session, handle);
            }
            session.save();
        } catch (IllegalStateException e) {
            LOG.warn("No depublish action for {}", node.getPath() , e);
        }
        return node.getPath();
    }

    String update(ContentNode contentNode, String location, Session session) {
        String updatedDocumentLocation = createOrUpdate(contentNode, location, session);
        publish(updatedDocumentLocation, session);
        return updatedDocumentLocation;
    }

    /**
     * Creates or updates the document's unpublished/draft variant, without publishing it.
     * Split out from update() so callers can act on that variant (e.g. assign a slug) before
     * publish() runs - once published, the published variant is not directly writable via
     * plain JCR (see the AccessDeniedException this avoids), only via workflow transitions,
     * which is what publish() itself uses to copy properties forward.
     */
    String createOrUpdate(ContentNode contentNode, String location, Session session) {
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
        return importTask.createOrUpdateDocumentFromVariantContentNode(
                contentNode, contentNode.getPrimaryType(), location, "en", contentNode.getName());
    }

    void publish(String location, Session session) {
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        try {
            documentManager.publishDocument(location);
        } catch (IllegalStateException e) {
            LOG.warn("No publish action for {}", location, e);
        }
    }

    /**
     * Sets the folder type only if it differs from the current value, to avoid triggering
     * a write (and JCR observation event) on shared ancestor folder nodes for every item
     * processed when the value is already correct.
     */
    boolean setFolderType(Node node, String type) throws RepositoryException {
        if (node.hasProperty("hippostd:foldertype")) {
            Value[] values = node.getProperty("hippostd:foldertype").getValues();
            if (values.length == 1 && type.equals(values[0].getString())) {
                return false;
            }
        }
        node.setProperty("hippostd:foldertype", new String [] { type });
        return true;
    }

    boolean setBooleanPropertyIfChanged(Node node, String name, boolean value) throws RepositoryException {
        if (node.hasProperty(name) && node.getProperty(name).getBoolean() == value) {
            return false;
        }
        node.setProperty(name, value);
        return true;
    }

    boolean setStringPropertyIfChanged(Node node, String name, String value) throws RepositoryException {
        if (node.hasProperty(name) && value.equals(node.getProperty(name).getString())) {
            return false;
        }
        node.setProperty(name, value);
        return true;
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
