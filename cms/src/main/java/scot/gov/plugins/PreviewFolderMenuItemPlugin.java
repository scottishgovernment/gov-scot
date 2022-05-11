package scot.gov.plugins;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.ExceptionDialog;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.repository.util.NodeIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.dialogs.PreviewDatePickerDialog;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashSet;
import java.util.Set;

import static org.hippoecm.repository.HippoStdNodeType.NT_DIRECTORY;
import static org.hippoecm.repository.HippoStdNodeType.NT_FOLDER;
import static org.hippoecm.repository.api.HippoNodeType.NT_HANDLE;

public class PreviewFolderMenuItemPlugin extends RenderPlugin {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PreviewFolderMenuItemPlugin.class);

    public PreviewFolderMenuItemPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        add(new StdWorkflow("menuItem", getMenuItemLabelModel(), getMenuItemIconResourceReference(), context, (WorkflowDescriptorModel) getModel()) {

            private static final long serialVersionUID = 1L;

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                try {
                    Set<String> nodeIDs = new HashSet<>();
                    folderPreviewGeneration(getModel().getNode(), nodeIDs);
                    return new PreviewDatePickerDialog(nodeIDs);
                } catch (RepositoryException e){
                    LOG.error("An exception occurred while trying to generate a preview link for a folder.");
                    return new ExceptionDialog(e);
                }
            }

        });
    }

    private void folderPreviewGeneration(final Node folder, final Set<String> nodeIDs) throws RepositoryException {
        for (Node child : new NodeIterable(folder.getNodes())) {
            if (child.isNodeType(NT_FOLDER) || child.isNodeType(NT_DIRECTORY)) {
                folderPreviewGeneration(child, nodeIDs);
            } else if (child.isNodeType(NT_HANDLE)) {
                if (isMirror(child)) {
                    nodeIDs.add(getMirrorTarget(child));
                }
                nodeIDs.add(child.getIdentifier());
            }
        }
    }

    boolean isMirror(Node node) throws RepositoryException {

        if (!node.hasNode(node.getName())) {
            return false;
        }

        Node doc = node.getNode(node.getName());
        return doc.isNodeType("publishing:mirror");
    }

    String getMirrorTarget(Node node) throws RepositoryException {
        return node.getNode(node.getName()).getNode("publishing:document").getProperty("hippo:docbase").getString();
    }

    protected IModel<String> getDialogTitleModel() {
        return new StringResourceModel("folder.action.preview.dialog.label", this, null);
    }

    protected IModel<String> getMenuItemLabelModel() {
        return new StringResourceModel("folder.action.preview.menuitem.label", this, null);
    }

    private ResourceReference getMenuItemIconResourceReference() {
        return new PackageResourceReference(getClass(), "preview-icon-16.png");
    }

}
