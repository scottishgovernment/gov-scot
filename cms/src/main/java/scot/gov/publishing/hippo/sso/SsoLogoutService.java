package scot.gov.publishing.hippo.sso;

import org.apache.wicket.request.flow.RedirectToUrlException;
import org.hippoecm.frontend.logout.CmsLogoutService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;

public class SsoLogoutService extends CmsLogoutService {

    public SsoLogoutService(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    protected void redirectPage() {
        throw new RedirectToUrlException("/logout");
    }

}
