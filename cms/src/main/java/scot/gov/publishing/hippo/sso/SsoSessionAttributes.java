package scot.gov.publishing.hippo.sso;

import org.hippoecm.frontend.model.UserCredentials;

public final class SsoSessionAttributes {

    static final String SSO = "sso";
    static final String CREDENTIALS = UserCredentials.class.getName();
    static final String STATE = "state";
    static final String NONCE = "nonce";
    static final String CODE_VERIFIER = "code_verifier";
    static final String SSO_ERROR = "sso_error";
    static final String RETURN_URL = "return_url";

    private SsoSessionAttributes() {
        // Constants only
    }

}
