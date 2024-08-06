package scot.gov.www;

import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.sluglookup.SlugLookups;

import javax.jcr.*;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.tika.utils.StringUtils.isBlank;

/**
 * Maintain the data structure used to lookup slugs.
 *
 * For each of news and publications, we maintain 2 trees of slugs: one for preview and one for live.
 * Each one is a tree of slugs broken down by letter where the leaf node maps to the content path for that item.
 * This allows the link processor to get the right path for a slug with a simple session.getNode call rather than
 * having to do an expensive query.
 */
public class SlugMaintenanceListener extends DaemonModuleBase {

    private static final Logger LOG = LoggerFactory.getLogger(SlugMaintenanceListener.class);

    static final String SLUG = "govscot:slug";

    private static final String PREVIEW = "preview";

    private static final String LIVE = "live";

    @Override
    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return true;
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {

        if (!session.nodeExists(event.subjectPath())) {
            return;
        }

        Node subject = session.getNode(event.subjectPath());
        if (!requiresSlug(subject)) {
            return;
        }

        handleEventByAction(event, subject);
    }

    void handleEventByAction(HippoWorkflowEvent event, Node subject) throws RepositoryException {
        switch (event.action()) {
            case "commitEditableInstance":
                updateLookup(subject, PREVIEW);
                break;

            case "publish" :
                updateLookup(subject, LIVE);
                break;

            case "depublish":
                removeLookup(subject, LIVE);
                break;

            case "delete":
                removeLookup(subject, PREVIEW);
                break;

            default:
        }
    }

    void updateLookup(Node subject, String mount ) throws RepositoryException {
        String slug = slug(subject);
        if (isBlank(slug)) {
            LOG.warn("Slug is empty for {}", slug);
            return;
        }

        String site = sitename(subject);
        String path = substringAfter(subject.getPath(), site);
        String type = path.split("/")[1];

        SlugLookups slugLookups = new SlugLookups(session);
        slugLookups.updateLookup(slug, path, site, type, mount, true);
        session.save();
    }

    void removeLookup(Node subject, String mount) throws RepositoryException {
        String site = sitename(subject);
        String path = substringAfter(subject.getPath(), site);
        String type = path.split("/")[1];

        SlugLookups slugLookups = new SlugLookups(session);
        slugLookups.removeLookup(path, site, type, mount);
        session.save();
    }

    private String slug(Node subject) throws RepositoryException {
        Node variant = subject.getNode(subject.getName());
        return variant.getProperty(SLUG).getString();
    }

    private String sitename(Node subject) throws RepositoryException {
        return subject.getAncestor(3).getName();
    }

    boolean requiresSlug(Node subject) throws RepositoryException {
        if (!subject.hasNode(subject.getName())) {
            return false;
        }

        Node variant = subject.getNode(subject.getName());
        return variant.hasProperty(SLUG);
    }

}
