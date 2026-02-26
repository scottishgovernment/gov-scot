package scot.gov.publishing.hippo.sso;

import org.hippoecm.frontend.model.UserCredentials;

public final class SsoSessionAttributes {

    /**
     * A flag attribute to indicate that the user is logged in via SSO.
     */
    static final String SSO = "sso";

    /**
     * The name of the credentials attribute. This is set in the OpenID
     * Connect callback and copied to a request attribute with the same
     * name by the OidcLoginFilter.
     */
    static final String CREDENTIALS = UserCredentials.class.getName();

    /**
     * The error query parameter returned from the IdP via the callback.
     * Values are defined in RFC 6749 §4.1.2.1 / OpenID Connect Core §3.1.2.6
     * (e.g. {@code access_denied}, {@code invalid_request}).
     * This is stored in the session, so it is available on the login page.
     */
    static final String SSO_ERROR = "sso_error";

    /**
     * Set when an internal error occurs processing the OIDC callback (e.g. a
     * network failure talking to the token endpoint, or a session that expired
     * before the callback arrived). Distinct from {@link #SSO_ERROR}, which
     * carries a spec-defined error code returned by the IdP itself.
     */
    static final String CALLBACK_ERROR = "callback_error";

    /**
     * The URL to return the user to after login. This is set prior to IdP
     * login, and the user is redirected to it after the IdP callback.
     */
    static final String RETURN_URL = "return_url";

    /**
     * A flag attribute to indicate that the user has logged out.
     * This is used to prevent the user from automatically being logged back in
     * after logging out. It distinguishes between the cases where the login
     * page is shown because the user logged out, and the user was not logged
     * in after following an IdP redirect.
     */
    static final String LOGGED_OUT = "logged_out";

    static final String STATE = "state";
    static final String NONCE = "nonce";
    static final String CODE_VERIFIER = "code_verifier";

    private SsoSessionAttributes() {
        // Constants only
    }

}
