package scot.gov.www;

import org.apache.wicket.Component;
import org.apache.wicket.model.StringResourceModel;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.icon.HippoIcon;
import org.hippoecm.frontend.service.IBrowseService;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.frontend.skin.Icon;
import org.hippoecm.repository.api.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Adds an "Image folder" item to the Document menu that ensures a gallery folder exists for
 * the document being edited (mirroring the logic used by {@link OrganiseGalleryImagesJob}),
 * then navigates the CMS document browser straight to it. Shown for any document under
 * {@value #PUBLICATIONS_PREFIX}, regardless of its specific document type (publications span
 * several doctypes, e.g. {@code govscot:Publication}, {@code govscot:Consultation}, etc).
 */
public class CreateGalleryFolderPlugin extends RenderPlugin<Workflow> {

    private static final Logger LOG = LoggerFactory.getLogger(CreateGalleryFolderPlugin.class);

    private static final String DEFAULT_BROWSER_ID = "service.browse";

    static final String PUBLICATIONS_PREFIX = GalleryFolderUtils.DOCUMENTS_PREFIX + "publications/";

    public CreateGalleryFolderPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        WorkflowDescriptorModel model = (WorkflowDescriptorModel) getDefaultModel();

        add(new StdWorkflow("creategalleryfolder", new StringResourceModel("menuitem.label", this, null),
                context, model) {

            @Override
            public String getSubMenu() {
                return "document";
            }

            @Override
            protected Component getIcon(String id) {
                return HippoIcon.fromSprite(id, Icon.FOLDER);
            }

            @Override
            public boolean isVisible() {
                return isUnderPublicationsFolder();
            }

            @Override
            protected void execute() throws Exception {
                Node handle = getModel().getNode();
                Node galleryFolder = GalleryFolderUtils.ensureGalleryFolderForDocument(
                        handle.getSession(), handle.getPath());
                handle.getSession().save();

                String browserId = config.getString(IBrowseService.BROWSER_ID, DEFAULT_BROWSER_ID);
                IBrowseService browseService = context.getService(browserId, IBrowseService.class);
                if (browseService != null) {
                    browseService.browse(new JcrNodeModel(galleryFolder));
                } else {
                    LOG.warn("No IBrowseService found under id {}, cannot navigate to {}",
                            browserId, galleryFolder.getPath());
                }
            }
        });
    }

    private boolean isUnderPublicationsFolder() {
        try {
            WorkflowDescriptorModel model = (WorkflowDescriptorModel) getDefaultModel();
            if (model == null || model.getNode() == null) {
                return false;
            }
            return model.getNode().getPath().startsWith(PUBLICATIONS_PREFIX);
        } catch (RepositoryException e) {
            LOG.error("Error checking document path", e);
            return false;
        }
    }
}
