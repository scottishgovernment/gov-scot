package scot.gov.publishing.hippo.sso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

@SuppressWarnings("unused")
public class SsoLoginPlugin extends CustomLoginPlugin {

    public SsoLoginPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    protected LoginPanel createLoginPanel(String id, LoginConfig config, LoginHandler handler) {
        SsoConfig ssoConfig = SsoConfig.get();
        if (ssoConfig.mode() == SsoConfig.Mode.OFF) {
            return super.createLoginPanel(id, config, handler);
        }
        return new SsoLoginForm(id, config, handler);
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
    }

    class SsoLoginForm extends DefaultLoginPlugin.TimeZonePanel {

        public SsoLoginForm(String id, LoginConfig config, LoginHandler handler) {
            super(id, config, handler);

            SsoConfig ssoConfig = SsoConfig.get();
            boolean credentialsVisible = ssoConfig.mode() == SsoConfig.Mode.OPTIONAL
                    && ssoConfig.enabled() == SsoConfig.Default.OFF;
            boolean credentialsAllowed = ssoConfig.mode() == SsoConfig.Mode.OPTIONAL;

            // SSO button — sets the SSO session attribute then redirects to the initialPath
            // (CMS) or path (console) query parameter, so OidcLoginFilter intercepts the
            // request and saves the correct RETURN_URL before redirecting to the IdP.
            // Uses AjaxButton to bypass PreventResubmit.js (which disables submit buttons
            // before POST serialisation, causing findSubmitter() to return null and the
            // default password-login handler to run instead).
            AjaxButton ssoLogin = new AjaxButton("sso-login", form) {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
                    HttpSession session = request.getSession(true);
                    session.setAttribute(SsoSessionAttributes.SSO, true);
                    // Ensure no stale credentials are in the session from a previous login.
                    // This forces and IdP redirect which, in turn, clears the session and
                    // ensures the application picks up the new credentials.
                    session.removeAttribute(SsoSessionAttributes.CREDENTIALS);
                    session.removeAttribute(SsoSessionAttributes.LOGGED_OUT);
                    target.appendJavaScript("window.location.href = window.location.href.replace('loginmessage=UserLoggedOut', '')"
                            + ".replace('?0&', '?');");
                }
            };
            ssoLogin.setDefaultFormProcessing(false);
            ssoLogin.add(new Label("sso-login-label", new ResourceModel("sso.login")));
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
