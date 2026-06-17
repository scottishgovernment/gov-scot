package scot.gov.www.archive;

import org.apache.wicket.Component;
import org.apache.wicket.model.StringResourceModel;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.ExceptionDialog;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.icon.HippoIcon;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.frontend.skin.Icon;
import org.hippoecm.repository.api.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.MarkupContainer;
import scot.gov.publishing.hippo.redirects.JcrRedirectRepository;
import scot.gov.publishing.hippo.redirects.PublicationArchiver;
import scot.gov.publishing.hippo.redirects.Redirect;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArchivePlugin extends RenderPlugin<Workflow> {

    private static final Logger LOG = LoggerFactory.getLogger(ArchivePlugin.class);

    private static final String ARCHIVERS_GROUP_PATH = "/hippo:configuration/hippo:groups/archivers";
    private static final String MEMBERS_PROPERTY = "hipposys:members";

    public ArchivePlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
        WorkflowDescriptorModel model = (WorkflowDescriptorModel) getDefaultModel();

        add(new StdWorkflow("archive", new StringResourceModel("create-archive-redirects", this, null), context, model) {

            @Override
            public String getSubMenu() {
                return "Archive";
            }

            @Override
            public boolean isVisible() {
                return isUserInArchivers() && isApplicableDocumentType();
            }

            @Override
            public boolean isEnabled() {
                try {
                    return !redirectExists(model.getNode());
                } catch (RepositoryException e) {
                    LOG.error("Error checking redirect existence for create item", e);
                    return false;
                }
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.LINK);
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                try {
                    Node handleNode = model.getNode();
                    return new ArchiveInputDialog(getPluginContext(), handleNode, (ArchiveResultsDialog.OnCloseCallback) ArchivePlugin.this::forceWorkflowMenuRebuild);
                } catch (RepositoryException e) {
                    LOG.error("Error getting document node for archive dialog", e);
                    return new ExceptionDialog(e);
                }
            }
        });

        add(new StdWorkflow("removeRedirects", new StringResourceModel("remove-redirects", this, null), context, model) {

            @Override
            public String getSubMenu() {
                return "Archive";
            }

            @Override
            public boolean isVisible() {
                return isUserInArchivers() && isApplicableDocumentType();
            }

            @Override
            public boolean isEnabled() {
                try {
                    return redirectExists(model.getNode());
                } catch (RepositoryException e) {
                    LOG.error("Error checking redirect existence for remove item", e);
                    return false;
                }
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.TIMES);
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                try {
                    Session session = UserSession.get().getJcrSession();
                    Node handleNode = model.getNode();
                    List<Redirect> removed = removeRedirects(session, handleNode);
                    int count = removed.size();
                    String noun = count == 1 ? "redirect" : "redirects";
                    return new ArchiveResultsDialog(removed, count + " " + noun + " removed",
                            (ArchiveResultsDialog.OnCloseCallback) ArchivePlugin.this::forceWorkflowMenuRebuild);
                } catch (RepositoryException e) {
                    LOG.error("Error removing redirects", e);
                    return new ExceptionDialog(e);
                }
            }
        });

        add(new StdWorkflow("showRedirects", new StringResourceModel("show-redirects", this, null), context, model) {

            @Override
            public String getSubMenu() {
                return "Archive";
            }

            @Override
            public boolean isVisible() {
                return isUserInArchivers() && isApplicableDocumentType();
            }

            @Override
            public boolean isEnabled() {
                try {
                    return redirectExists(model.getNode());
                } catch (RepositoryException e) {
                    LOG.error("Error checking redirect existence for show item", e);
                    return false;
                }
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.SEARCH);
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                try {
                    Session session = UserSession.get().getJcrSession();
                    Node handleNode = model.getNode();
                    List<Redirect> redirects = lookupRedirects(session, handleNode);
                    int count = redirects.size();
                    String noun = count == 1 ? "redirect" : "redirects";
                    return new ArchiveResultsDialog(redirects, count + " " + noun + " for this page",
                            (ArchiveResultsDialog.OnCloseCallback) () -> {});
                } catch (RepositoryException e) {
                    LOG.error("Error looking up redirects", e);
                    return new ExceptionDialog(e);
                }
            }
        });
    }

    private boolean redirectExists(Node handleNode) throws RepositoryException {
        Session session = UserSession.get().getJcrSession();
        JcrRedirectRepository repo = new JcrRedirectRepository(session);
        String documentPath = ArchiveDocumentUtils.getDocumentPath(handleNode);
        return repo.lookup(documentPath).isPresent();
    }

    private List<Redirect> removeRedirects(Session session, Node handleNode) throws RepositoryException {
        JcrRedirectRepository repo = new JcrRedirectRepository(session);
        String documentPath = ArchiveDocumentUtils.getDocumentPath(handleNode);

        List<String> pathsToDelete = new ArrayList<>();
        pathsToDelete.add(documentPath);

        if (ArchiveDocumentUtils.isPublication(handleNode)) {
            // Regenerate the full list of publication page paths using a dummy redirect
            Redirect dummy = new Redirect();
            dummy.setFrom(documentPath);
            dummy.setTo("dummy");
            dummy.setHistoricalUrl(true);
            PublicationArchiver archiver = new PublicationArchiver(session, repo);
            List<Redirect> expanded = archiver.expand(dummy);
            for (Redirect r : expanded) {
                pathsToDelete.add(r.getFrom());
            }
        }

        List<Redirect> removed = new ArrayList<>();
        for (String path : pathsToDelete) {
            Optional<Redirect> existing = repo.lookup(path);
            if (existing.isPresent() && repo.delete(path)) {
                removed.add(existing.get());
            }
        }
        return removed;
    }

    private List<Redirect> lookupRedirects(Session session, Node handleNode) throws RepositoryException {
        JcrRedirectRepository repo = new JcrRedirectRepository(session);
        String documentPath = ArchiveDocumentUtils.getDocumentPath(handleNode);

        List<String> pathsToLookup = new ArrayList<>();
        pathsToLookup.add(documentPath);

        if (ArchiveDocumentUtils.isPublication(handleNode)) {
            Redirect dummy = new Redirect();
            dummy.setFrom(documentPath);
            dummy.setTo("dummy");
            dummy.setHistoricalUrl(true);
            PublicationArchiver archiver = new PublicationArchiver(session, repo);
            List<Redirect> expanded = archiver.expand(dummy);
            for (Redirect r : expanded) {
                pathsToLookup.add(r.getFrom());
            }
        }

        List<Redirect> found = new ArrayList<>();
        for (String path : pathsToLookup) {
            repo.lookup(path).ifPresent(found::add);
        }
        return found;
    }

    /**
     * Forces the document workflow menu to re-evaluate isEnabled()/isVisible() on all items
     * without a full page reload. Walks up the Wicket component hierarchy to find
     * DocumentWorkflowManagerPlugin and sets its private {@code updateMenu} field to true
     * via reflection, which triggers a menu rebuild on the next Ajax response.
     */
    private void forceWorkflowMenuRebuild() {
        MarkupContainer parent = getParent();
        while (parent != null) {
            try {
                java.lang.reflect.Field field = parent.getClass().getDeclaredField("updateMenu");
                field.setAccessible(true);
                field.setBoolean(parent, true);
                return;
            } catch (NoSuchFieldException e) {
                parent = parent.getParent();
            } catch (IllegalAccessException e) {
                LOG.warn("Could not set updateMenu on DocumentWorkflowManagerPlugin", e);
                return;
            }
        }
        LOG.debug("DocumentWorkflowManagerPlugin not found in parent hierarchy; menu state updates on next page load");
    }

    private boolean isApplicableDocumentType() {
        try {
            WorkflowDescriptorModel model = (WorkflowDescriptorModel) getDefaultModel();
            return ArchiveDocumentUtils.isPublication(model.getNode());
        } catch (RepositoryException e) {
            LOG.error("Error checking document type for Archive menu visibility", e);
            return false;
        }
    }

    private boolean isUserInArchivers() {
        try {
            Session session = UserSession.get().getJcrSession();
            String userId = session.getUserID();
            if (!session.nodeExists(ARCHIVERS_GROUP_PATH)) {
                return false;
            }
            Node groupNode = session.getNode(ARCHIVERS_GROUP_PATH);
            if (!groupNode.hasProperty(MEMBERS_PROPERTY)) {
                return false;
            }
            for (Value member : groupNode.getProperty(MEMBERS_PROPERTY).getValues()) {
                if (userId.equals(member.getString())) {
                    return true;
                }
            }
            return false;
        } catch (RepositoryException e) {
            LOG.error("Error checking archivers group membership", e);
            return false;
        }
    }
}
