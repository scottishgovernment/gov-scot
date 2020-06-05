package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.sitemenu.HstSiteMenu;
import org.hippoecm.hst.core.sitemenu.HstSiteMenuItem;
import org.onehippo.cms7.essentials.components.EssentialsMenuComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsMenuComponentInfo;

/**
 * Subclass the standard menu component in order to feature flag items.
 */
@ParametersInfo(type = EssentialsMenuComponentInfo.class)
public class MainMenu extends EssentialsMenuComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HstSiteMenu menu = (HstSiteMenu) request.getAttribute("menu");
        if (menu == null) {
            return;
        }

        // set feature flags for site items
        for (HstSiteMenuItem siteMenuItem : menu.getSiteMenuItems()) {
            String flagName = flagname(siteMenuItem);
            boolean enabled = FeatureFlags.isEnabled(flagName, request.getRequestContext(), true);
            request.setAttribute(flagName, enabled);
        }
    }

    String flagname(HstSiteMenuItem menuItem) {
        return menuItem.getName().replaceAll(" ", "") + "Menu";
    }
}
