package scot.gov.publishing.hippo.sso;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

public class OIDCLoginFilter extends HttpFilter {

    public static final String CREDENTIALS_ATTR_NAME = UserCredentials.class.getName();

    private static final Logger LOG = LoggerFactory.getLogger(OIDCLoginFilter.class);

    private static final Scope SCOPE = new Scope("openid", "profile", "email");

    private static final List<String> EXCLUDED_PREFIXES = List.of(
            "/angular/",
            "/ckeditor/",
            "/favicon.ico",
            "/navapp-assets/",
            "/skin/",
            "/login",
            "/ping/",
            "/sso/",
            "/wicket/",
            "/ws/redirects"
    );

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/ws/navigationitems",
            "/ws/indexexport"
    );

    private String clientId;
    private String redirectUri;
    private String authorizationEndpoint;
    private boolean configured = false;

    private synchronized void ensureConfigured() throws ServletException {
        if (configured) {
            return;
        }
        ComponentManager componentManager = HstServices.getComponentManager();
        ContainerConfiguration config = componentManager.getContainerConfiguration();
        this.clientId = config.getString("oidc.client.id");
        this.redirectUri = config.getString("oidc.redirect.uri");
        this.authorizationEndpoint = config.getString("oidc.authorization.endpoint");

        if (ObjectUtils.anyNull(clientId, redirectUri, authorizationEndpoint)) {
            throw new ServletException("Not configured");
        }
        this.configured = true;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (isExcluded(request)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("id_token") != null) {
            LOG.info("Already authed {}", request.getServletPath());
            Object creds = session.getAttribute(CREDENTIALS_ATTR_NAME);
            request.setAttribute(CREDENTIALS_ATTR_NAME, creds);
            chain.doFilter(request, response);
            return;
        }

        sendRedirect(request, response);
    }

    private void sendRedirect(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ensureConfigured();

        // Redirect to OIDC provider
        State state = new State();
        Nonce nonce = new Nonce();
        HttpSession session = req.getSession(true);
        CodeVerifier codeVerifier = new CodeVerifier();
        session.setAttribute("code_verifier", codeVerifier);
        session.setAttribute("state", state);
        session.setAttribute("nonce", nonce);

        AuthenticationRequest authRequest = new AuthenticationRequest.Builder(
                new ResponseType(ResponseType.Value.CODE),
                SCOPE,
                new ClientID(clientId),
                URI.create(redirectUri))
                .endpointURI(URI.create(authorizationEndpoint))
                .codeChallenge(codeVerifier, CodeChallengeMethod.S256)
                .state(state)
                .responseType(ResponseType.CODE)
                .nonce(nonce)
                .build();

        String url = authRequest.toURI().toString();
        LOG.info("Redirecting to {}", url);
        res.sendRedirect(url);
    }

    private static boolean isExcluded(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String path = requestURI.substring(contextPath.length());
        return EXCLUDED_PREFIXES.stream().anyMatch(path::startsWith)
                || EXCLUDED_PATHS.contains(path);
    }

}
