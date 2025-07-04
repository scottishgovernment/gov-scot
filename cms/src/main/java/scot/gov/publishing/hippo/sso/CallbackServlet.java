package scot.gov.publishing.hippo.sso;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hippoecm.frontend.model.UserCredentials;

import javax.jcr.SimpleCredentials;
import java.io.IOException;
import java.net.URI;

import static java.util.Collections.emptyList;

public class CallbackServlet implements Filter {

    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";
    private static final String REDIRECT_URI = "https://lcl.publishing.gov.scot/callback";
    private static final String TOKEN_ENDPOINT = "https://cloudhothouse.okta.com/oauth2/default/v1/token";
    private static final String USERINFO_ENDPOINT = "https://cloudhothouse.okta.com/oauth2/default/v1/userinfo";
    private static final JWSAlgorithm EXPECTED_JWS_ALG = JWSAlgorithm.RS256;

    public static final String CREDENTIALS_ATTR_NAME = UserCredentials.class.getName();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (!req.getServletPath().startsWith("/callback")) {
            chain.doFilter(req, res);
            return;
        }
        doGet(req, res);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("/callback " + req.getServletPath());
        HttpSession session = req.getSession();

        String code = req.getParameter("code");
        String stateParam = req.getParameter("state");

        if (!stateParam.equals(session.getAttribute("state").toString())) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid state");
            return;
        }

        // Exchange code for tokens
        AuthorizationCode codeObj = new AuthorizationCode(code);
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(codeObj, URI.create(REDIRECT_URI));
        ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET));
        TokenRequest tokenRequest = new TokenRequest(URI.create(TOKEN_ENDPOINT), clientAuth, codeGrant);

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

        UserInfoRequest userInfoRequest = new UserInfoRequest(URI.create(USERINFO_ENDPOINT), successResponse.getOIDCTokens().getBearerAccessToken());
        HTTPResponse httpResponse = userInfoRequest.toHTTPRequest().send();
        UserInfoResponse userInfoResponse = null;
        try {
            userInfoResponse = UserInfoResponse.parse(httpResponse);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (httpResponse.indicatesSuccess()) {
            UserInfo info = userInfoResponse.toSuccessResponse().getUserInfo();
            System.out.println(info);
        }

        OIDCTokens tokens = successResponse.getOIDCTokens();
        JWT idToken = tokens.getIDToken();

        try {
            JWTClaimsSet jwtClaimsSet = idToken.getJWTClaimsSet();
            String email = (String) jwtClaimsSet.getClaim("email");
            char[] password = {0};
            SimpleCredentials creds = new SimpleCredentials(email, password);
            creds.setAttribute(SsoAttributes.SAML_ID, email);
            creds.setAttribute(SsoAttributes.SAML_GROUPS, emptyList());
            session.setAttribute(CREDENTIALS_ATTR_NAME, new UserCredentials(creds));

            // Optionally verify ID Token (signature, audience, expiration, etc.)
            session.setAttribute("id_token", idToken);
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

