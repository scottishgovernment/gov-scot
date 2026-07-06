package scot.gov.www.importer.sink;

import org.onehippo.forge.content.pojo.model.ContentNode;
import scot.gov.www.importer.domain.PressRelease;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class PublicationSink extends AbstractSink {

    Session session;

    ContentNodes contentNodes = new ContentNodes();

    Locations locations = new Locations();

    String type;

    String pathElement;

    String action;

    public PublicationSink(Session session, String type, String pathElement, String action) {
        this.session = session;
        this.type = type;
        this.pathElement = pathElement;
        this.action = action;
    }

    @Override
    public void acceptPressRelease(PressRelease release) throws RepositoryException {
        ContentNode contentNode = contentNodes.publication(release, type, session);
        String location = locations.publicationLocation(release, pathElement, session);
        update(contentNode, location, session);
        postProcessPublication(release, location, "new-publication-month-folder", action, session);
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

    @Override
    public void removeDeletedPressRelease(String id) throws RepositoryException {
        //Not implemented
    }

}
