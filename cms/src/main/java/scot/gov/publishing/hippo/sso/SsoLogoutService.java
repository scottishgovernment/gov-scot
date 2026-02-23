package scot.gov.publishing.hippo.sso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hippoecm.frontend.logout.CmsLogoutService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;

/**
 * Sets a session attribute when the user logs out.
 * This attribute is used to prevent the user from being automatically logged back in.
 */
@SuppressWarnings("unused")
public class SsoLogoutService extends CmsLogoutService {

    public SsoLogoutService(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    protected void logoutSession() {
        // Invalidate the existing session
        super.logoutSession();

        // Set "logged out" attribute on a new session.
        Request request = RequestCycle.get().getRequest();
        HttpServletRequest req = (HttpServletRequest) request.getContainerRequest();
        HttpSession session = req.getSession(true);
        session.setAttribute(SsoSessionAttributes.LOGGED_OUT, true);
    }

}
