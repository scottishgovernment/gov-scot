package scot.gov.www;

import org.onehippo.repository.events.HippoWorkflowEvent;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Allocate a slug to any new news then assign a slug to it.
 *
 * If the name already exists then disambiguate the slug by adding a number to the end.
 */
public class NewsSlugDaemonModule extends SlugDaemonModule {

    private static final String DOCUMENT_TYPE = "govscot:News";

    protected static final String PRGLOO_SLUG_PROPERTY = "govscot:prglooslug";

    private static final String PREFIX = "/content/documents/govscot/news/";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return
            "add".equals(event.action())
            && event.success()
            && isNewsPath(event.result());
    }

    /**
     * Is this a news item path? For example
     *
     * /content/documents/govscot/news/2018/12/test
     */
    private boolean isNewsPath(String path) {
        // the length should be 9 (the leading slash means the first entry is the empty string)
        return path.startsWith(PREFIX) && path.split("/").length == 9;
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        Node news = session.getNode(event.result());
        if (news == null) {
            return;
        }
        assignSlug(news);
    }

    private void assignSlug(Node newsNode) throws RepositoryException {
        String name = newsNode.getName();
        if (newsNode.hasProperty(PRGLOO_SLUG_PROPERTY)) {
            name = newsNode.getProperty(PRGLOO_SLUG_PROPERTY).getString();
        }

        String slug = allocate(name, DOCUMENT_TYPE);
        newsNode.setProperty(GOVSCOT_SLUG_PROPERTY, slug);
        session.save();
    }

}

