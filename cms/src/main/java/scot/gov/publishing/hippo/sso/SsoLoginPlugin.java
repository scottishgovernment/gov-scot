package scot.gov.publishing.hippo.sso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.login.DefaultLoginPlugin;
import org.hippoecm.frontend.plugins.login.LoginConfig;
import org.hippoecm.frontend.plugins.login.LoginHandler;
import org.hippoecm.frontend.plugins.login.LoginPanel;
import org.onehippo.forge.resetpassword.frontend.ResetPasswordConst;
import org.onehippo.forge.resetpassword.login.CustomLoginPlugin;
import org.slf4j.LoggerFactory;

public class SsoLoginPlugin extends CustomLoginPlugin {

    public SsoLoginPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    protected LoginPanel createLoginPanel(String id, LoginConfig config, LoginHandler handler) {
        return new SSOLoginForm(id, config, handler);
    }

    class SSOLoginForm extends DefaultLoginPlugin.TimeZonePanel {

        public SSOLoginForm(String id, LoginConfig config, LoginHandler handler) {
            super(id, config, handler);
            form.addLabelledComponent(new Label("forgot-password-label", new ResourceModel("forgot.password.label")));
            setCookieValue(ResetPasswordConst.LOCALE_COOKIE, selectedLocale, ResetPasswordConst.LOCALE_COOKIE_MAXAGE);

            form.addLabelledComponent(new Label("sso-login", new ResourceModel("sso.login")));
        }

        @Override
        protected void loginSuccess() {
            super.loginSuccess();
            LoggerFactory.getLogger(getClass()).info("Session ID: {}", getSession().getId());
            HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
            HttpSession session = request.getSession();
            session.setAttribute("sso", false);
        }
    }

}
