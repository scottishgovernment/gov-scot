package scot.gov.www.importer.sink;

import org.onehippo.forge.content.pojo.model.ContentNode;
import scot.gov.www.importer.domain.PressRelease;

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
        String location = locations.newsLocation(release, session);
        String slug = newsSideEffects.resolveSlug(session, location, release.getSeoName());
        ContentNode contentNode = contentNodes.news(release, session, slug);
        String updatedlocation = update(contentNode, location, session);
        ensureNewsFolderActions(updatedlocation, session);
        newsSideEffects.afterPublish(session, session.getNode(updatedlocation));
        detectMissingSlug(session, updatedlocation, release);
    }

    void ensureNewsFolderActions(String location, Session session) throws RepositoryException {
        Node handle = session.getNode(location);
        Node month = handle.getParent();
        Node year = month.getParent();
        Node news = year.getParent();
        boolean changed = setFolderType(month, "new-news-document");
        changed |= setFolderType(year, "new-news-month-folder");
        changed |= setBooleanPropertyIfChanged(year, "hippostd:hasfolders", true);
        changed |= setBooleanPropertyIfChanged(news, "hippostd:hasfolders", true);
        if (changed) {
            session.save();
        }
    }

    @Override
    public void removeDeletedPressRelease(String id) throws RepositoryException {
        //Not implemented
    }
}
