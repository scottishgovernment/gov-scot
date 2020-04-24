package scot.gov.www;

import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Allocate a slug to any new news then assign a slug to it.
 *
 * If the name already exists then disambiguate the slug by adding a number to the end.
 */
public class NewsSlugDaemonModule extends SlugDaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(NewsSlugDaemonModule.class);

    private static final String DOCUMENT_TYPE = "govscot:News";

    protected static final String PRGLOO_SLUG_PROPERTY = "govscot:prglooslug";

    private static final String PREFIX = "/content/documents/govscot/news/";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success() && event.subjectPath().startsWith(PREFIX);
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {

        Node handle = session.getNode(event.returnValue()).getParent();

        if (handle == null) {
            LOG.info("handle was null: {}", event.subjectPath());
            return;
        }
        Node news = getLatestVariant(handle);
        assignSlug(news);
    }

    private void assignSlug(Node newsNode) throws RepositoryException {
        String name = newsNode.getName();
        if (newsNode.hasProperty(PRGLOO_SLUG_PROPERTY)) {
            name = newsNode.getProperty(PRGLOO_SLUG_PROPERTY).getString();
        }

        String slug = allocate(name, DOCUMENT_TYPE);
        LOG.info("assignSlug {} -> {}", newsNode.getPath(), slug);
        newsNode.setProperty(GOVSCOT_SLUG_PROPERTY, slug);
        session.save();
    }

}

