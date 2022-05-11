package scot.gov.plugins;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
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
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.api.HippoNodeType;
import org.hippoecm.repository.api.Workflow;
import org.hippoecm.repository.api.WorkflowException;
import org.hippoecm.repository.api.WorkflowManager;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.dialogs.PreviewDatePickerDialog;
import scot.gov.dialogs.PreviewLink;
import scot.gov.dialogs.PreviewLinkDialog;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class PreviewPlugin extends RenderPlugin<Workflow> {

    private static final Logger LOG = LoggerFactory.getLogger(PreviewPlugin.class);
    private boolean isPreview;

    public static final String INTERNAL_PREVIEW_NODE_NAME = "previewId";

    public PreviewPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
        WorkflowDescriptorModel model = (WorkflowDescriptorModel) getDefaultModel();
        if (model != null) {
            try {
                Node node = model.getNode();
                if (node.isNodeType(HippoNodeType.NT_HANDLE)) {
                    WorkflowManager workflowManager = UserSession.get().getWorkflowManager();
                    DocumentWorkflow workflow = (DocumentWorkflow) workflowManager.getWorkflow(model.getObject());
                    Serializable isPreviewAvailable = workflow.hints().get("previewAvailable");
                    isPreview = (isPreviewAvailable instanceof Boolean) && ((Boolean) isPreviewAvailable);
                }
            } catch (RepositoryException | RemoteException | WorkflowException e) {
                LOG.error("Error getting document node from WorkflowDescriptorModel", e);
            }
        }

        add(new InternalShareWorkflow("preview.generation", new StringResourceModel("generate", this, null), getPluginContext(), model));

        add(new StdWorkflow("preview.overview", new StringResourceModel("overview", this, null), getPluginContext(), model) {

            @Override
            public String getSubMenu() {
                return "Preview Link";
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.INFO_CIRCLE);
            }

            @Override
            public boolean isEnabled() {
                return previewLinkExists();
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                try {
                    return new PreviewLinkDialog(getPreviewLinks());
                } catch (RepositoryException exception) {
                    LOG.warn("Problems while generating url ", exception);
                    return new ExceptionDialog(exception);
                }
            }
        });

    }

    protected List<PreviewLink> getPreviewLinks() throws RepositoryException {
        List<PreviewLink> previewLinks = new ArrayList<>();
        Node unpublishedVariant = retrieveUnpublishedVariant();
        if (unpublishedVariant != null) {
            NodeIterator iterator = unpublishedVariant.getNodes(INTERNAL_PREVIEW_NODE_NAME);
            while (iterator.hasNext()) {
                Node node = iterator.nextNode();
                String uuid = JcrUtils.getStringProperty(node, "staging:key", "");
                String url = JcrUtils.getStringProperty(node, "staging:url", "");
                String username = JcrUtils.getStringProperty(node, "staging:user", null);
                Calendar creationCalendar = JcrUtils.getDateProperty(node, "staging:creationdate", null);
                Calendar expirationCalendar = JcrUtils.getDateProperty(node, "staging:expirationdate", null);
                if(StringUtils.isNotEmpty(uuid)){
                    previewLinks.add(new PreviewLink(uuid, url, username, creationCalendar , expirationCalendar, isValid(expirationCalendar), node));
                }
            }
        }
        return previewLinks;
    }

    private boolean isValid(final Calendar expirationCalendar){
        return expirationCalendar == null || Calendar.getInstance().before(expirationCalendar);
    }

    protected Node retrieveUnpublishedVariant() throws RepositoryException{
        WorkflowDescriptorModel model = (WorkflowDescriptorModel) getDefaultModel();
        Node documentNode = model.getNode();

        return getUnpublishedVariant(documentNode);
    }

    private static Node getUnpublishedVariant(Node handle) throws RepositoryException {
        for (Node variant : new NodeIterable(handle.getNodes(handle.getName()))) {
            final String state = JcrUtils.getStringProperty(variant, HippoStdNodeType.HIPPOSTD_STATE, null);
            if (HippoStdNodeType.UNPUBLISHED.equals(state)) {
                return variant;
            }
        }
        return null;
    }

    private boolean previewLinkExists() {
        try {
            Node unpublishedNode = retrieveUnpublishedVariant();
            if(unpublishedNode != null) {
                return unpublishedNode.hasNode(INTERNAL_PREVIEW_NODE_NAME);
            }
        } catch (RepositoryException e) {
            LOG.error("Exception while performing check for preview link existence.", e);
        }
        return false;
    }

    private class InternalShareWorkflow extends StdWorkflow {
        private static final long serialVersionUID = 1L;
        private final String actionName;

        public InternalShareWorkflow(String id, IModel<String> name, IPluginContext pluginContext, WorkflowDescriptorModel model) {
            super(id, name, null, pluginContext, model);
            this.actionName = name.getObject();
        }

        public String getActionName() {
            return actionName;
        }

        @Override
        public String getSubMenu() {
            return "Preview Link";
        }

        @Override
        public boolean isVisible() {
            return isPreview;
        }

        @Override
        protected IModel<String> getTitle() {
            return new LoadableDetachableModel<String>() {
                private static final long serialVersionUID = 1L;

                @Override
                protected String load() {
                    return getActionName() + "...";
                }
            };
        }

        @Override
        protected IDialogService.Dialog createRequestDialog() {
            try {
                return new PreviewDatePickerDialog(Collections.singleton(getModel().getNode().getIdentifier()));
            } catch (RepositoryException e){
                LOG.error("An exception occurred while trying to generate a preview link for a single document.", e);
                return new ExceptionDialog(e);
            }
        }

    }

}
