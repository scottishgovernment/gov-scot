package scot.gov.publishing.hippo.sso;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hippoecm.frontend.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.SimpleCredentials;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public class CallbackHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CallbackHandler.class);

    OidcConfig oidcConfig;
    boolean configured = false;
    private IDTokenValidator idTokenValidator;

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            ensureConfigured();
            handleCallback(req, resp);
        } catch (CallbackException ex) {
            LOG.error(ex.getMessage(), ex.getCause());
            redirectWithError(req, resp);
        }
    }

    private synchronized void ensureConfigured() throws CallbackException {
        try {
            if (!configured) {
                configure();
                this.configured = true;
            }
        } catch (GeneralException | IOException | JOSEException ex) {
            throw new CallbackException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to initialise", ex);
        }
    }

    private void configure() throws GeneralException, IOException, JOSEException {
        this.oidcConfig = OidcConfig.get();
        Issuer issuer = new Issuer(oidcConfig.issuer());
        this.idTokenValidator = new IDTokenValidator(
                issuer,
                oidcConfig.clientId(),
                JWSAlgorithm.RS256,
                oidcConfig.jwksUri().toURL());
    }

    private void handleCallback(HttpServletRequest req, HttpServletResponse resp) throws CallbackException, IOException {
        HttpSession session = req.getSession();
        String returnUrl = Objects.toString(
                session.getAttribute(SsoSessionAttributes.RETURN_URL),
                req.getContextPath() + "/");

        String error = req.getParameter("error");
        if (error != null) {
            handleErrorCallback(req, error);
        } else {
            handleSuccessCallback(req);
        }
        resp.sendRedirect(returnUrl);
    }

    private static void handleErrorCallback(HttpServletRequest req, String error) {
        String description = req.getParameter("error_description");
        LOG.warn("IdP returned error: {} ({})", error, description);
        req.getSession().setAttribute(SsoSessionAttributes.SSO_ERROR, error);
    }

    private void handleSuccessCallback(HttpServletRequest req) throws CallbackException {
        String stateParam = queryParameter(req, "state");
        String code = queryParameter(req, "code");

        HttpSession session = req.getSession();
        State state = sessionAttribute(session, SsoSessionAttributes.STATE, State.class);
        CodeVerifier codeVerifier = sessionAttribute(session, SsoSessionAttributes.CODE_VERIFIER, CodeVerifier.class);
        Nonce expectedNonce = sessionAttribute(session, SsoSessionAttributes.NONCE, Nonce.class);

        if (!stateParam.equals(state.getValue())) {
            throw new CallbackException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid state (mismatched)");
        }

        // Exchange code for tokens
        OIDCTokenResponse successResponse = getTokenResponse(code, codeVerifier);

        // Validate and extract JWT claim set from exchanged tokens
        OIDCTokens tokens = successResponse.getOIDCTokens();
        JWT idToken = tokens.getIDToken();
        JWTClaimsSet jwtClaimsSet = parseJwtClaimSet(idToken, expectedNonce);
        LOG.debug("JWT claims set: {}", jwtClaimsSet);

        // Fetch user info
        BearerAccessToken accessToken = successResponse.getOIDCTokens().getBearerAccessToken();
        UserInfo userInfo = fetchUserInfo(accessToken);

        // Invalidate session to ensure Bloomreach creates a new session.
        // This is necessary for PluginUserSession to pick up the new credentials.
        session.invalidate();
        session = req.getSession();

        session.setAttribute(SsoSessionAttributes.SSO, true);
        storeInSession(session, userInfo);
    }

    private String queryParameter(HttpServletRequest request, String parameterName) throws CallbackException {
        String parameter = request.getParameter(parameterName);
        if (parameter == null) {
            throw new CallbackException(HttpServletResponse.SC_UNAUTHORIZED, "Request did not contain parameter " + parameterName);
        }
        return parameter;
    }

    private <T> T sessionAttribute(HttpSession session, String attributeName, Class<T> type) throws CallbackException {
        T attribute = type.cast(session.getAttribute(attributeName));
        if (attribute == null) {
            throw new CallbackException(HttpServletResponse.SC_UNAUTHORIZED, "Session did not contain attribute " + attributeName);
        }
        return attribute;
    }

    private OIDCTokenResponse getTokenResponse(String code, CodeVerifier codeVerifier) throws CallbackException {
        // Exchange code for tokens
        AuthorizationCode codeObj = new AuthorizationCode(code);
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(
                codeObj,
                URI.create(oidcConfig.redirectUri()),
                codeVerifier);

        TokenRequest tokenRequest = new TokenRequest.Builder(
                oidcConfig.tokenEndpoint(),
                oidcConfig.clientAuthentication().get(),
                codeGrant).build();
        try {
            HTTPResponse tokenHttpResponse = tokenRequest.toHTTPRequest().send();
            return OIDCTokenResponse.parse(tokenHttpResponse);
        } catch (IOException ex) {
            throw new CallbackException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Token exchange IO error", ex);
        } catch (ParseException ex) {
            throw new CallbackException(HttpServletResponse.SC_BAD_REQUEST, "Token exchange failed", ex);
        }
    }

    private UserInfo fetchUserInfo(BearerAccessToken accessToken) throws CallbackException {
        UserInfoRequest userInfoRequest = new UserInfoRequest(oidcConfig.userInfoEndpoint(), accessToken);
        try {
            HTTPResponse httpResponse = userInfoRequest.toHTTPRequest().send();
            UserInfo userInfo = UserInfoSuccessResponse.parse(httpResponse).getUserInfo();
            LOG.info("UserInfo: {}", userInfo);
            return userInfo;
        } catch (IOException ex) {
            throw new CallbackException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User info IO error", ex);
        } catch (ParseException ex) {
            throw new CallbackException(HttpServletResponse.SC_BAD_REQUEST, "User info request failed", ex);
        }
    }

    private JWTClaimsSet parseJwtClaimSet(JWT idToken, Nonce expectedNonce) throws CallbackException {
        try {
            // Check signature, issuer, audience, expiry, and nonce
            return idTokenValidator
                    .validate(idToken, expectedNonce)
                    .toJWTClaimsSet();
        } catch (ParseException ex) {
            // Building JWT claims set failed
            throw new CallbackException(HttpServletResponse.SC_BAD_REQUEST, "Invalid claim set", ex);
        } catch (BadJOSEException ex) {
            // Signature invalid, token expired, wrong audience, nonce mismatch, etc.
            throw new CallbackException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid ID token", ex);
        } catch (JOSEException ex) {
            // Cryptographic error
            throw new CallbackException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Token validation error", ex);
        }
    }

    private static void storeInSession(HttpSession session, UserInfo userInfo) {
        String email = userInfo.getEmailAddress();
        LOG.info("Authenticated as: {} {}", email, userInfo);
        char[] password = {0};
        SimpleCredentials creds = new SimpleCredentials(email, password);
        creds.setAttribute(SsoAttributes.SSO_ID, email);
        creds.setAttribute(SsoAttributes.SSO_EMAIL, email);
        creds.setAttribute(SsoAttributes.SSO_NAME, userInfo.getName());
        creds.setAttribute(SsoAttributes.SSO_GIVEN_NAME, userInfo.getGivenName());
        creds.setAttribute(SsoAttributes.SSO_FAMILY_NAME, userInfo.getFamilyName());
        session.setAttribute(SsoSessionAttributes.CREDENTIALS, new UserCredentials(creds));
    }

    private static void redirectWithError(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String returnUrl = Objects.toString(
                session.getAttribute(SsoSessionAttributes.RETURN_URL),
                req.getContextPath() + "/");
        // Clear OIDC flow attributes from the failed attempt — they are single-use and
        // tied to this now-failed flow, so leaving them would only add debugging noise.
        session.removeAttribute(SsoSessionAttributes.STATE);
        session.removeAttribute(SsoSessionAttributes.NONCE);
        session.removeAttribute(SsoSessionAttributes.CODE_VERIFIER);
        session.setAttribute(SsoSessionAttributes.CALLBACK_ERROR, true);
        resp.sendRedirect(returnUrl);
    }

    static class CallbackException extends Exception {

        private final int status;

        CallbackException(int status, String message) {
            this(status, message, null);
        }

        CallbackException(int status, String message, Exception ex) {
            super(message, ex);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

}
