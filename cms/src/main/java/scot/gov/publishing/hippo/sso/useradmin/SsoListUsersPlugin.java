package scot.gov.publishing.hippo.sso.useradmin;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.cms.admin.users.ListUsersPlugin;
import org.hippoecm.frontend.plugins.cms.admin.users.UserDataProvider;
import org.hippoecm.frontend.plugins.standards.panelperspective.breadcrumb.PanelPluginBreadCrumbPanel;

/**
 * Extends the ListUsers plugin to allow use of a custom panel to create users.
 */
@SuppressWarnings("unused")
public class SsoListUsersPlugin extends ListUsersPlugin {

    private final UserDataProvider userDataProvider = new UserDataProvider();

    public SsoListUsersPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    public PanelPluginBreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
        return new SsoListUsersPanel(
                componentId,
                this.getPluginContext(),
                this.getPluginConfig(),
                breadCrumbModel,
                this.userDataProvider);
    }

}
