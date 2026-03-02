package scot.gov.publishing.hippo.sso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.login.DefaultLoginPlugin;
import org.hippoecm.frontend.plugins.login.LoginConfig;
import org.hippoecm.frontend.plugins.login.LoginHandler;
import org.hippoecm.frontend.plugins.login.LoginPanel;
import org.onehippo.forge.resetpassword.frontend.ResetPasswordConst;
import org.onehippo.forge.resetpassword.login.CustomLoginPlugin;

@SuppressWarnings("unused")
public class SsoLoginPlugin extends CustomLoginPlugin {

    private final OidcConfig oidcConfig = OidcConfig.get();

    public SsoLoginPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    protected LoginPanel createLoginPanel(String id, LoginConfig config, LoginHandler handler) {
        SsoConfig ssoConfig = SsoConfig.get();
        if (ssoConfig.form() == SsoConfig.Form.NATIVE) {
            return super.createLoginPanel(id, config, handler);
        }
        return new SsoLoginPanel(id, config, handler);
    }

    private static void checkSsoErrors(LoginPanel panel) {
        HttpServletRequest httpRequest = (HttpServletRequest) panel.getRequest().getContainerRequest();
        HttpSession httpSession = httpRequest.getSession(false);
        if (httpSession != null &&
                httpSession.getAttribute(SsoSessionAttributes.SSO) != null &&
                httpSession.getAttribute(SsoSessionAttributes.CREDENTIALS) != null) {
            panel.getSession().error(panel.getString("sso.user.not.found"));
        }
        Object ssoError = httpSession != null ? httpSession.getAttribute(SsoSessionAttributes.SSO_ERROR) : null;
        if (ssoError != null) {
            httpSession.removeAttribute(SsoSessionAttributes.SSO_ERROR);
            panel.getSession().error(panel.getString("sso.idp.error"));
        }
        Object callbackError = httpSession != null ? httpSession.getAttribute(SsoSessionAttributes.CALLBACK_ERROR) : null;
        if (callbackError != null) {
            httpSession.removeAttribute(SsoSessionAttributes.CALLBACK_ERROR);
            panel.getSession().error(panel.getString("sso.callback.error"));
        }
    }

    class SsoLoginPanel extends DefaultLoginPlugin.TimeZonePanel {

        private final String returnUrl;

        public SsoLoginPanel(String id, LoginConfig config, LoginHandler handler) {
            super(id, config, handler);
            this.returnUrl = pageUrl();

            SsoConfig ssoConfig = SsoConfig.get();
            SsoConfig.Form ssoForm = ssoConfig.form();
            boolean credentialsAllowed = ssoForm != SsoConfig.Form.SSO;     // true for REVEAL or EXPANDED
            boolean credentialsVisible = ssoForm == SsoConfig.Form.EXPANDED;

            // Use AjaxButton to bypass PreventResubmit.js, which disables submit buttons
            // before POST serialization. In turn, this causes Form.findSubmitter() to return
            // null and the password onSubmit() runs instead.
            AjaxButton ssoLogin = ssoLoginButton();
            form.addLabelledComponent(ssoLogin);

            // Choice separator — visible when both SSO and login with username/password is available
            WebMarkupContainer choiceSeparator = new WebMarkupContainer("choice-separator");
            choiceSeparator.add(new Label("choice-separator-label", new ResourceModel("choice.separator")));
            choiceSeparator.setVisible(credentialsAllowed);
            choiceSeparator.setOutputMarkupPlaceholderTag(true);
            form.add(choiceSeparator);

            // Single credentials container for AJAX toggling
            WebMarkupContainer credentialsContainer = new WebMarkupContainer("credentials-container");
            credentialsContainer.setVisible(credentialsVisible);
            credentialsContainer.setOutputMarkupPlaceholderTag(true);

            // Reparent username and password into the credentials container
            Component usernameField = form.get("username");
            form.remove(usernameField);
            credentialsContainer.add(usernameField);

            Component passwordField = form.get("password");
            form.remove(passwordField);
            credentialsContainer.add(passwordField);

            // Forgot password link
            WebMarkupContainer forgotPasswordContainer = new WebMarkupContainer("forgot-password-container");
            forgotPasswordContainer.add(new Label("forgot-password-label", new ResourceModel("forgot.password.label")));
            credentialsContainer.add(forgotPasswordContainer);

            form.add(credentialsContainer);

            // Hide locale and timezone
            form.get("locale").setVisible(false);
            form.get("timezone").setVisible(false);

            // Set locale cookie (duplicated from CustomLoginPlugin.CustomLoginForm which is package-private)
            setCookieValue(ResetPasswordConst.LOCALE_COOKIE, selectedLocale, ResetPasswordConst.LOCALE_COOKIE_MAXAGE);

            // Relabel existing submit button to "Login with password"
            Component submitButton = form.get("submit");
            submitButton.get("submit-label").setDefaultModel(new ResourceModel("password.login"));
            submitButton.setVisible(credentialsVisible);
            submitButton.setOutputMarkupPlaceholderTag(true);

            // Password toggle button — visible when credentials are hidden and mode is OPTIONAL
            AjaxButton passwordToggle = new AjaxButton("password-toggle", form) {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    credentialsContainer.setVisible(true);
                    submitButton.setVisible(true);
                    choiceSeparator.setVisible(false);
                    this.setVisible(false);
                    target.add(credentialsContainer, submitButton, choiceSeparator, this);
                    target.focusComponent(usernameField);
                }
            };
            passwordToggle.setDefaultFormProcessing(false);
            passwordToggle.setVisible(credentialsAllowed && !credentialsVisible);
            passwordToggle.setOutputMarkupPlaceholderTag(true);
            passwordToggle.add(new Label("password-toggle-label", new ResourceModel("password.login")));
            form.add(passwordToggle);
        }

        private static String pageUrl() {
            HttpServletRequest httpRequest = (HttpServletRequest) RequestCycle.get()
                    .getRequest()
                    .getContainerRequest();
            String requestURI = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();
            return queryString == null ? requestURI : requestURI + "?" + queryString;
        }

        private AjaxButton ssoLoginButton() {
            AjaxButton ssoLogin = new AjaxButton("sso-login", form) {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
                    HttpSession session = request.getSession(true);
                    session.setAttribute(SsoSessionAttributes.SSO, true);
                    // Ensure no stale credentials are in the session from a previous login.
                    // This forces an IdP redirect which, in turn, clears the session and
                    // ensures the application picks up the new credentials.
                    session.removeAttribute(SsoSessionAttributes.CREDENTIALS);
                    session.removeAttribute(SsoSessionAttributes.LOGGED_OUT);
                    // Save the URL of the login page as RETURN_URL, captured when the page
                    // was first rendered, so CallbackHandler can return the user here after
                    // authentication.
                    session.setAttribute(SsoSessionAttributes.RETURN_URL, returnUrl);
                    // Build the IdP authorization URL and navigate the browser to it.
                    String idpUrl = new RedirectHandler(oidcConfig).buildRedirectUrl(session);
                    target.appendJavaScript("window.location.href = '" + idpUrl + "';");
                }
            };
            ssoLogin.add(new Label("sso-login-label", new ResourceModel("sso.login")));
            ssoLogin.setDefaultFormProcessing(false);
            return ssoLogin;
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            checkSsoErrors(this);
        }


        @Override
        protected void loginSuccess() {
            super.loginSuccess();
            HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
            HttpSession session = request.getSession(true);
            session.removeAttribute(SsoSessionAttributes.SSO);
            session.removeAttribute(SsoSessionAttributes.LOGGED_OUT);
        }
    }

}
