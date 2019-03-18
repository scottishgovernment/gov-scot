package scot.gov.www;

import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

/**
 * Allocate a slug to any new publication (including the subtypes and complex document) then assign a slug to it.
 *
 * If the folder name already exists then disambiguate the slug by adding a number to the end.
 */
public class PublicationSlugDaemonModule implements DaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationSlugDaemonModule.class);

    private static final String PUBLICATION_SLUG_PROPERTY = "govscot:slug";

    private static final String INDEX = "index";

    private static final String PREFIX = "/content/documents/govscot/publications/";

    private Session session;

    @Override
    public void initialize(Session session) throws RepositoryException {
        this.session = session;
        HippoServiceRegistry.registerService(this, HippoEventBus.class);
    }

    @Override
    public void shutdown() {
        HippoServiceRegistry.unregisterService(this, HippoEventBus.class);
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {

        if (!event.subjectPath().startsWith(PREFIX)) {
            return;
        }

        if (!event.success()) {
            return;
        }

        try {
            if (!isNewPublicationFolder(event)) {
                return;
            }
            Node handle = session.getNode(event.returnValue()).getNode(INDEX);

            if (handle == null) {
                LOG.info("handle was null: {}", event.subjectPath());
                return;
            }
            Node publication = getLatestVariant(handle);
            assignSlug(publication);
        } catch (RepositoryException ex) {
            LOG.error("Could not set publication slug for {}", ex, event.subjectPath());
        }
    }

    private void assignSlug(Node publication) throws RepositoryException {
        String folderName = folderName(publication);
        String slug = allocate(folderName);
        LOG.info("assignSlug {} -> {}", publication.getPath(), slug);
        publication.setProperty(PUBLICATION_SLUG_PROPERTY, slug);
        session.save();
    }

    public String allocate(String slug) throws RepositoryException {
        // If it does not already exist then just use this slug.
        if (!slugAlreadyExists(slug)) {
            return slug;
        }

        // The slug is already used, try appending a number starting from 2
        // i.e. rather than try my-document-1 we will start from my-document-2.
        return disambiguate(slug, 2);
    }

    /**
     * Recursively try adding a number to the end of the slug until we get a unique one.
     */
    private String disambiguate(String slug, int postfix) throws RepositoryException {
        String candidate = String.format("%s-%d", slug, postfix);

        if (!slugAlreadyExists(candidate)) {
            // Base case: we have found a unique slug.
            return candidate;
        }

        // Recursive call to try the next number.
        return disambiguate(slug, postfix + 1);
    }

    private boolean slugAlreadyExists(String slug) throws RepositoryException {
        String sql = String.format("SELECT * FROM govscot:SimpleContent WHERE govscot:slug = '%s'", slug);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL).execute();
        return result.getNodes().hasNext();
    }

    private String folderName(Node publication) throws RepositoryException {
        return publication.getParent().getParent().getName();
    }

    /**
     * Is this event a new publication folder being added?
     */
    private boolean isNewPublicationFolder(HippoWorkflowEvent event) {
        return isFolderAddEvent(event) && isPublicationPath(event.returnValue());
    }

    /**
     * Is this event a new folder being added?
     */
    private boolean isFolderAddEvent(HippoWorkflowEvent event) {
        return
                event.arguments() != null &&
                event.arguments().contains("hippostd:folder") &&
                "threepane:folder-permissions:add".equals(event.interaction());
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

    private static Node getLatestVariant(Node handle) throws RepositoryException {
        NodeIterator it = handle.getNodes();
        Node variant = null;
        while (it.hasNext()) {
            variant = it.nextNode();
        }
        return variant;
    }
}

