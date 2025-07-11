package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.hippoecm.hst.core.sitemenu.HstSiteMenu;
import org.onehippo.cms7.essentials.components.EssentialsMenuComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsMenuComponentInfo;

import jakarta.servlet.ServletContext;

/**
 * Subclass the standard menu component in order to feature flag items.
 */
@ParametersInfo(type = EssentialsMenuComponentInfo.class)
public class MainMenu extends EssentialsMenuComponent {

    private boolean hideSearch = false;

    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        String hideSearchString = componentConfig.getRawParameters().getOrDefault("hideSearchToolbar", "false");
        hideSearch = "true".equals(hideSearchString);
    }

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HstSiteMenu menu = (HstSiteMenu) request.getAttribute("menu");
        if (menu == null) {
            return;
        }

        request.setAttribute("hideSearch", hideSearch);
    }

}