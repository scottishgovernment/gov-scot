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

    protected static final String PRGLOO_SLUG_PROPERTY = "govscot:prglooslug";

    private static final String PREFIX = "/content/documents/govscot/news/";

    // the JCR user the vuelio-importer impersonates as (see VuelioImporterJob) - the importer
    // assigns slugs to news items itself (see NewsSideEffects), since this listener's own save
    // would run on a separate session that nothing the importer does can ever cause to persist
    private static final String IMPORTER_USER = "news";

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        if (!("add".equals(event.action()) && event.success() && isNewsPath(event.result()))) {
            return false;
        }

        boolean isImporterEvent = IMPORTER_USER.equals(event.user());
        if (isImporterEvent) {
            LOG.warn("NewsSlugDaemonModule: skipping {} - triggered by importer user '{}', importer assigns its own slug",
                    event.result(), event.user());
        } else {
            LOG.warn("NewsSlugDaemonModule: handling {} - triggered by user '{}'", event.result(), event.user());
        }
        return !isImporterEvent;
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
            LOG.warn("news is null for event, not allocating a slug {}, arguments {}", event.result(), event.arguments());
            return;
        }
        assignSlug(news);
    }

    private void assignSlug(Node newsNode) throws RepositoryException {
        String name = newsNode.getName();
        if (newsNode.hasProperty(PRGLOO_SLUG_PROPERTY)) {
            name = newsNode.getProperty(PRGLOO_SLUG_PROPERTY).getString();
        }

        String slug = allocate(name, "news");
        newsNode.setProperty(GOVSCOT_SLUG_PROPERTY, slug);
        session.save();
    }

}

