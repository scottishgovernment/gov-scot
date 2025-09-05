package scot.gov.www;

import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;
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

    SlugLookups slugLookups;

    HippoUtils hippoUtils = new HippoUtils();

    @Override
    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return true;
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {

        if (isFolderMove(event)) {
            updateLookupsInFolderForFolderMove(event);
            return;
        }

        if (isFolderCopy(event)) {
            updateLookupsInFolderForFolderCopy(event);
            return;
        }

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

    void updateLookup(Node subject, String mount, boolean clearLookout) throws RepositoryException {
        String slug = slug(subject);
        String site = sitename(subject);
        String path = substringAfter(subject.getPath(), site);
        slugLookups.updateLookup(slug, path, site, "global", mount, clearLookout);
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

    boolean isFolderMove(HippoWorkflowEvent event) {
        if (!"moveFolder".equals(event.action())) {
            return false;
        }

        return "threepane:folder-permissions:moveFolder".equals(event.interaction());
    }

    boolean isFolderCopy(HippoWorkflowEvent event) {
        if (!"copyFolder".equals(event.action())) {
            return false;
        }

        return "threepane:folder-permissions:copyFolder".equals(event.interaction());
    }

    void updateLookupsInFolderForFolderMove(HippoWorkflowEvent event) throws RepositoryException {
        String sitename = getSitenameFromSubjectPath(event.subjectPath());
        String fromPath = substringAfter(event.subjectPath(), sitename);
        String toFolder = session.getNodeByIdentifier(event.arguments().get(2).toString()).getPath();
        String toPath = substringAfter(toFolder, sitename) + "/" + event.arguments().get(3);
        updateUrlLookupsForFolderMove(sitename, fromPath, toPath);
        session.save();
    }

    void updateUrlLookupsForFolderMove(String sitename, String fromPath, String toPath) throws RepositoryException{
        String xpath = String.format(
                "/jcr:root/content/urls/%s//element(*, sluglookup:lookup)[jcr:like(@sluglookup:path, '%s/%%')]",
                sitename,
                fromPath);
        hippoUtils.executeXpathQuery(session, xpath, node -> {
            String oldPath = node.getProperty("sluglookup:path").getString();
            String newPath = oldPath.replace(fromPath, toPath);
            LOG.error("setting {} -> {}", oldPath, newPath);
            node.setProperty("sluglookup:path", newPath);
        });
    }

    void updateLookupsInFolderForFolderCopy(HippoWorkflowEvent event) throws RepositoryException {
        String sitename = getSitenameFromSubjectPath(event.subjectPath());
        String toFolder = session.getNodeByIdentifier(event.arguments().get(2).toString()).getPath();
        String toPath = substringAfter(toFolder, sitename) + "/" + event.arguments().get(3);
        String xpath = String.format(
                "/jcr:root/content/documents/%s%s//*[govscot:slug != '']",
                sitename,
                toPath);

        hippoUtils.executeXpathQuery(session, xpath, node -> {
            String slug = node.getProperty(SLUG).getString();
            String newSlug = slug + "-copy";
            node.setProperty(SLUG, newSlug);
            session.save();
            updateLookup(node.getParent(), PREVIEW, false);
        });
    }

    String getSitenameFromSubjectPath(String path) {
        return path.split("/")[3];
    }

}
