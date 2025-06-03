package scot.gov.publishing.hippo.sso;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.ObjectUtils;
import org.hippoecm.frontend.model.UserCredentials;
import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.SimpleCredentials;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import static java.util.Collections.emptyList;

public class CallbackHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CallbackHandler.class);

    public static final String CREDENTIALS_ATTR_NAME = UserCredentials.class.getName();

    private ClientID clientId;
    private String clientSecret;
    private String redirectUri;
    private URI tokenEndpoint;
    private URI userInfoEndpoint;
    private boolean configured = false;
    private IDTokenValidator idTokenValidator;

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ensureConfigured();
            handleCallback(req, resp);
        } catch (CallbackException ex) {
            LOG.error(ex.getMessage(), ex.getCause());
            resp.sendError(ex.getStatus(), ex.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/");
    }

    private synchronized void ensureConfigured() throws CallbackException {
        try {
            if (!configured) {
                configure();
                this.configured = true;
            }
        } catch (GeneralException | IOException ex) {
            throw new CallbackException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to initialise", ex);
        }
    }

    private void configure() throws GeneralException, IOException {
        ComponentManager componentManager = HstServices.getComponentManager();
        ContainerConfiguration config = componentManager.getContainerConfiguration();

        String issuerUri = config.getString("oidc.issuer");
        Issuer issuer = new Issuer(issuerUri);
        URI jwksEndpoint;

        if (false) {
            jwksEndpoint = URI.create(config.getString("oidc.jwks.uri"));
            this.tokenEndpoint = URI.create(config.getString("oidc.token.endpoint"));
            this.userInfoEndpoint = URI.create(config.getString("oidc.userinfo.endpoint"));
        } else {
            OIDCProviderMetadata metadata = OIDCProviderMetadata.resolve(issuer);
            jwksEndpoint = metadata.getJWKSetURI();
            this.tokenEndpoint = metadata.getTokenEndpointURI();
            this.userInfoEndpoint = metadata.getUserInfoEndpointURI();
        }
        this.clientId = new ClientID(config.getString("oidc.client.id"));
        this.clientSecret = config.getString("oidc.client.secret");
        this.redirectUri = config.getString("oidc.redirect.uri");

        this.idTokenValidator = new IDTokenValidator(
                issuer,
                clientId,
                JWSAlgorithm.RS256,
                jwksEndpoint.toURL());
    }

    private void handleCallback(HttpServletRequest req, HttpServletResponse resp) throws CallbackException {
        LOG.info("/callback {}", req.getServletPath());
        HttpSession session = req.getSession();

        String code = req.getParameter("code");
        String stateParam = req.getParameter("state");
        String state = Objects.toString(session.getAttribute("state"), null);
        CodeVerifier codeVerifier = (CodeVerifier) session.getAttribute("code_verifier");
        Nonce expectedNonce = (Nonce) session.getAttribute("nonce");

        validateParametersAndSession(code, stateParam, state, codeVerifier, expectedNonce);

        // Exchange code for tokens
        OIDCTokenResponse successResponse = getTokenResponse(code, codeVerifier);

        // Validate and extract JWT claim set from exchanged tokens
        OIDCTokens tokens = successResponse.getOIDCTokens();
        JWT idToken = tokens.getIDToken();
        JWTClaimsSet jwtClaimsSet = parseJwtClaimSet(idToken, expectedNonce);

        // Fetch user info
        BearerAccessToken accessToken = successResponse.getOIDCTokens().getBearerAccessToken();
        UserInfo userInfo = fetchUserInfo(accessToken);

        storeInSession(session, jwtClaimsSet, userInfo, idToken);
    }

    private void validateParametersAndSession(String code, String stateParam, String state, CodeVerifier codeVerifier, Nonce expectedNonce) throws CallbackException {
        if (ObjectUtils.anyNull(code, stateParam)) {
            throw new CallbackException(HttpServletResponse.SC_UNAUTHORIZED, "Code and state are required");
        }

        if (!stateParam.equals(state)) {
            throw new CallbackException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid state");
        }

        if (codeVerifier == null) {
            throw new CallbackException(HttpServletResponse.SC_UNAUTHORIZED, "Code verifier not present");
        }

        if (expectedNonce == null) {
            throw new CallbackException(HttpServletResponse.SC_UNAUTHORIZED, "Expected nonce not present");
        }
    }

    private OIDCTokenResponse getTokenResponse(String code, CodeVerifier codeVerifier) throws CallbackException {
        // Exchange code for tokens
        AuthorizationCode codeObj = new AuthorizationCode(code);
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(
                codeObj,
                URI.create(redirectUri),
                codeVerifier);

        ClientAuthentication clientAuth = new ClientSecretBasic(clientId, new Secret(clientSecret));
        TokenRequest tokenRequest = new TokenRequest.Builder(tokenEndpoint, clientAuth, codeGrant).build();
        TokenResponse tokenResponse = null;
        try {
            HTTPResponse tokenHttpResponse = tokenRequest.toHTTPRequest().send();
            tokenResponse = OIDCTokenResponseParser.parse(tokenHttpResponse);
        } catch (IOException ex) {
            throw new CallbackException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Token exchange IO error", ex);
        } catch (ParseException ex) {
            throw new CallbackException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid token response", ex);
        }

        if (!tokenResponse.indicatesSuccess()) {
            throw new CallbackException(HttpServletResponse.SC_BAD_REQUEST, "Token exchange failed");
        }

        return (OIDCTokenResponse) tokenResponse.toSuccessResponse();
    }

    private UserInfo fetchUserInfo(BearerAccessToken accessToken) throws CallbackException {
        UserInfoRequest userInfoRequest = new UserInfoRequest(userInfoEndpoint, accessToken);
        HTTPResponse userInfoHttpResponse = null;
        UserInfoResponse userInfoResponse = null;
        try {
            userInfoHttpResponse = userInfoRequest.toHTTPRequest().send();
            userInfoResponse = UserInfoResponse.parse(userInfoHttpResponse);
        } catch (IOException ex) {
            throw new CallbackException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User info IO error", ex);
        } catch (ParseException ex) {
            throw new CallbackException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Fetching user info failed", ex);
        }

        if (!userInfoHttpResponse.indicatesSuccess()) {
            throw new CallbackException(HttpServletResponse.SC_BAD_REQUEST, "User info request failed");
        }

        UserInfo userInfo = userInfoResponse.toSuccessResponse().getUserInfo();
        LOG.info("UserInfo: {}", userInfo);
        return userInfo;
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

    private static void storeInSession(HttpSession session, JWTClaimsSet jwtClaimsSet, UserInfo userInfo, JWT idToken) {
        String email = (String) jwtClaimsSet.getClaim("email");
        LOG.info("Authenticated as: {} {} {}", email, jwtClaimsSet, userInfo);
        char[] password = {0};
        SimpleCredentials creds = new SimpleCredentials(email, password);
        creds.setAttribute(SsoAttributes.SSO_ID, email);
        creds.setAttribute(SsoAttributes.SSO_GROUPS, emptyList());
        creds.setAttribute(SsoAttributes.SSO_NAME, userInfo.getName());
        creds.setAttribute(SsoAttributes.SSO_GIVEN_NAME, userInfo.getGivenName());
        creds.setAttribute(SsoAttributes.SSO_FAMILY_NAME, userInfo.getFamilyName());
        session.setAttribute(CREDENTIALS_ATTR_NAME, new UserCredentials(creds));

        session.setAttribute("id_token", idToken);
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
