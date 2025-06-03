package scot.gov.publishing.hippo.sso.useradmin;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.cms.admin.users.ListUsersPanel;
import org.hippoecm.frontend.plugins.cms.admin.users.UserDataProvider;
import org.hippoecm.frontend.plugins.standards.panelperspective.breadcrumb.PanelPluginBreadCrumbLink;

/**
 * Extends the default ListUsersPanel to allow use of a custom form for creating users.
 */
public class SsoListUsersPanel extends ListUsersPanel {

    public SsoListUsersPanel(String id, IPluginContext context, IPluginConfig config, IBreadCrumbModel breadCrumbModel, UserDataProvider userDataProvider) {
        super(id, context, config, breadCrumbModel, userDataProvider);
        PanelPluginBreadCrumbLink originalLink =
                (PanelPluginBreadCrumbLink) get("create-user-button-container:create-user-link");

        PanelPluginBreadCrumbLink newLink = new PanelPluginBreadCrumbLink("create-user-link", breadCrumbModel) {
            @Override
            protected IBreadCrumbParticipant getParticipant(final String componentId) {
                return new SsoCreateUserPanel(componentId, breadCrumbModel, context, config);
            }
        };
        newLink.setVisible(originalLink.isVisible());

        WebMarkupContainer container = (WebMarkupContainer) get("create-user-button-container");
        container.replace(newLink);
    }

}
