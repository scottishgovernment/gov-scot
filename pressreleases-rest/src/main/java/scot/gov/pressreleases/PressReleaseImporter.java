package scot.gov.pressreleases;

import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.pressreleases.domain.PressRelease;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

public class PressReleaseImporter {

    private static final Logger LOG = LoggerFactory.getLogger(PressReleaseImporter.class);

    static final String GOVSCOT_TITLE = "govscot:title";

    static final String HIPPO_DOCBASE = "hippo:docbase";

    static final String GOVSCOT_NEWS = "govscot:News";

    static final String GOVSCOT_PUBLICATION = "govscot:Publication";

    private Locations locations = new Locations();

    private ContentNodes contentNodes = new ContentNodes();

    public String importPressRelease(PressRelease release, Session session) throws RepositoryException {
        ContentNode contentNode = contentNodes.news(release, session);
        String location = locations.newsLocation(release, session);
        String updatedlocation = update(contentNode, location, session);
        ensureNewsFolderActions(updatedlocation, session);
        return updatedlocation;
    }

    public String importSpeech(PressRelease release, Session session) throws RepositoryException {
        ContentNode contentNode = contentNodes.publication(release, "govscot:SpeechOrStatement", session);
        String location = locations.publicationLocation(release, "speech-statement", session);
        String updatedLocation = update(contentNode, location, session);
        postProcessPublication(release, location, "new-publication-month-folder", "new-speech-or-statement-folder", session);
        return updatedLocation;
    }

    public String importCorrespondence(PressRelease release, Session session) throws RepositoryException {
        ContentNode contentNode = contentNodes.publication(release, GOVSCOT_PUBLICATION, session);
        String location = locations.publicationLocation(release, "correspondence", session);
        String updatedLocation = update(contentNode, location, session);
        postProcessPublication(release, location, "new-publication-month-folder", "new-publication-folder", session);
        return updatedLocation;
    }

    public String deleteNews(String id, Session session) throws RepositoryException {
        return delete(id, GOVSCOT_NEWS, session);
    }

    public String deletePublication(String id, Session session) throws RepositoryException {
        return delete(id, GOVSCOT_PUBLICATION, session);
    }

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

    void ensureNewsFolderActions(String location, Session session) throws RepositoryException {
        Node handle = session.getNode(location);
        Node month = handle.getParent();
        Node year = month.getParent();
        setFolderType(month, "new-news-document");
        setFolderType(year, "new-news-month-folder");
        session.save();
    }

    void postProcessPublication(PressRelease release, String location, String yearType, String monthType, Session session) throws RepositoryException {
        Node handle = session.getNode(location);
        handle.setProperty("hippo:name", release.getTitle());
        Node pub = handle.getParent();
        Node month = pub.getParent();
        Node year = month.getParent();
        setFolderType(month, monthType);
        setFolderType(year, yearType);
        removeExtraIndex(pub);
        session.save();
    }

    void removeExtraIndex(Node pubfolder) throws RepositoryException {
        NodeIterator it = pubfolder.getNodes("index");
        while (it.hasNext()) {
            Node handle = it.nextNode();
            if (handle.getNodes("index").getSize() != 3) {
                handle.remove();
                return;
            }
        }
    }

    void setFolderType(Node node, String type) throws RepositoryException {
        node.setProperty("hippostd:foldertype", new String [] { type });
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
}
