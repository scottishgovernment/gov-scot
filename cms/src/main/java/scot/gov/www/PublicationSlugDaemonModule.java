package scot.gov.www;

import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * Allocate a slug to any new publication (including the subtypes and complex document) then assign a slug to it.
 *
 * If the folder name already exists then disambiguate the slug by adding a number to the end.
 */
public class PublicationSlugDaemonModule extends SlugDaemonModule {


    private static final Logger LOG = LoggerFactory.getLogger(PublicationSlugDaemonModule.class);

    private static final String PUBLICATION_DOCUMENT_TYPE = "govscot:Publication";

    private static final String INDEX = "index";

    private static final String PREFIX = "/content/documents/govscot/publications/";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success() && event.subjectPath().startsWith(PREFIX) && isNewPublicationFolder(event);
    }

    private boolean isNewPublicationFolder(HippoWorkflowEvent event) {
        return isFolderAddEvent(event) && isPublicationPath(event.returnValue());
    }

    private boolean isFolderAddEvent(HippoWorkflowEvent event) {
        return
                event.arguments() != null &&
                        event.arguments().contains("hippostd:folder") &&
                        "add".equals(event.action());
    }

    /**
     * Is this a publication path?
     *
     * Publications path look like:
     * /content/documents/govscot/publications/correspondence/2018/12/test
     *
     * They start with the publication folder and have a type, year, month and publication subfolder
     */
    private boolean isPublicationPath(String path) {
        // the length should be 9 (the leading slash means the first entry is the empty string)
        return path.startsWith(PREFIX) && path.split("/").length == 9;
    }


    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {

        Node returnVal  = session.getNode(event.returnValue());
        if (!returnVal.hasNode(INDEX)) {
            return;
        }
        Node handle = returnVal.getNode(INDEX);

        if (handle == null) {
            LOG.info("handle was null: {}", event.subjectPath());
            return;
        }
        Node publication = getLatestVariant(handle);
        assignSlug(publication);
    }

    private void assignSlug(Node publication) throws RepositoryException {
        String folderName = folderName(publication);
        String slug = allocate(folderName, PUBLICATION_DOCUMENT_TYPE);
        LOG.info("assignSlug {} -> {}", publication.getPath(), slug);
        publication.setProperty(GOVSCOT_SLUG_PROPERTY, slug);
        session.save();
    }

    private String folderName(Node publication) throws RepositoryException {
        return publication.getParent().getParent().getName();
    }

}