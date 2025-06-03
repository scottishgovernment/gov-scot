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
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import jakarta.servlet.ServletException;
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

    public void handleRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ensureConfigured();
        doGet(req, res);
    }

    private synchronized void ensureConfigured() throws ServletException {
        try {
            if (!configured) {
                configure();
                this.configured = true;
            }
        } catch (GeneralException | IOException e) {
            throw new ServletException(e);
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

    private void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("/callback {}", req.getServletPath());
        HttpSession session = req.getSession();

        String code = req.getParameter("code");
        String stateParam = req.getParameter("state");

        if (ObjectUtils.anyNull(code, stateParam)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Code and state are required");
            return;
        }

        String state = Objects.toString(session.getAttribute("state"), null);
        if (!stateParam.equals(state)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid state");
            return;
        }

        CodeVerifier codeVerifier = (CodeVerifier) session.getAttribute("code_verifier");
        if (codeVerifier == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Code verifier not present");
            return;
        }

        // Exchange code for tokens
        AuthorizationCode codeObj = new AuthorizationCode(code);
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(
                codeObj,
                URI.create(redirectUri),
                codeVerifier);
        ClientAuthentication clientAuth = new ClientSecretBasic(clientId, new Secret(clientSecret));
        TokenRequest tokenRequest = new TokenRequest.Builder(
                tokenEndpoint,
                clientAuth,
                codeGrant).build();

        TokenResponse tokenResponse = null;
        try {
            tokenResponse = OIDCTokenResponseParser.parse(tokenRequest.toHTTPRequest().send());
        } catch (ParseException e) {
            throw new IOException(e);
        }

        if (!tokenResponse.indicatesSuccess()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token exchange failed");
            return;
        }

        OIDCTokenResponse successResponse = (OIDCTokenResponse) tokenResponse.toSuccessResponse();
        UserInfoRequest userInfoRequest = new UserInfoRequest(userInfoEndpoint, successResponse.getOIDCTokens().getBearerAccessToken());
        HTTPResponse httpResponse = userInfoRequest.toHTTPRequest().send();
        UserInfoResponse userInfoResponse = null;
        try {
            userInfoResponse = UserInfoResponse.parse(httpResponse);
        } catch (ParseException e) {
            throw new ServletException(e);
        }

        if (httpResponse.indicatesSuccess()) {
            UserInfo info = userInfoResponse.toSuccessResponse().getUserInfo();
            LOG.info("UserInfo: {}", info);
        }

        OIDCTokens tokens = successResponse.getOIDCTokens();
        JWT idToken = tokens.getIDToken();

        try {
            // Retrieve the nonce that was sent in the authentication request
            Nonce expectedNonce = (Nonce) session.getAttribute("nonce");
            // Check signature, issuer, audience, expiry, and nonce
            JWTClaimsSet jwtClaimsSet = idTokenValidator
                    .validate(idToken, expectedNonce)
                    .toJWTClaimsSet();

            String email = (String) jwtClaimsSet.getClaim("email");
            LOG.info("Authenticated as: {}", email);
            char[] password = {0};
            SimpleCredentials creds = new SimpleCredentials(email, password);
            creds.setAttribute(SsoAttributes.SSO_ID, email);
            creds.setAttribute(SsoAttributes.SSO_GROUPS, emptyList());
            session.setAttribute(CREDENTIALS_ATTR_NAME, new UserCredentials(creds));

            session.setAttribute("id_token", idToken);
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (ParseException e) {
            // Building JWT claims set failed
            throw new ServletException(e);
        } catch (BadJOSEException e) {
            // Signature invalid, token expired, wrong audience, nonce mismatch, etc.
            LOG.error("ID token validation failed", e);
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid ID token");
        } catch (JOSEException e) {
            // Cryptographic error
            LOG.error("ID token verification error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Token verification error");
        }
    }
}
