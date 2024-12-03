package scot.gov.www.pressreleases.sink;

import org.onehippo.forge.content.pojo.model.ContentNode;
import scot.gov.www.pressreleases.domain.PressRelease;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class NewsSink extends AbstractSink {

    Session session;

    ContentNodes contentNodes = new ContentNodes();

    Locations locations = new Locations();

    public NewsSink(Session session) {
        this.session = session;
    }

    @Override
    public void acceptPressRelease(PressRelease release) throws RepositoryException {
        ContentNode contentNode = contentNodes.news(release, session);
        String location = locations.newsLocation(release, session);
        String updatedlocation = update(contentNode, location, session);
        ensureNewsFolderActions(updatedlocation, session);
        detectMissingSlug(session, updatedlocation, release);
    }

    void ensureNewsFolderActions(String location, Session session) throws RepositoryException {
        Node handle = session.getNode(location);
        Node month = handle.getParent();
        Node year = month.getParent();
        setFolderType(month, "new-news-document");
        setFolderType(year, "new-news-month-folder");
        session.save();
    }

    @Override
    public void removeDeletedPressRelease(String id) throws RepositoryException {
        delete(id, "govscot:News", session);
    }
}
