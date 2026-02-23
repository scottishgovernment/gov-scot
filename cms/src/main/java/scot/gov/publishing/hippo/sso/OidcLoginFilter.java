package scot.gov.publishing.hippo.sso;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OidcLoginFilter extends HttpFilter {

    public static final String CREDENTIALS_ATTR_NAME = SsoSessionAttributes.CREDENTIALS;

    private static final Logger LOG = LoggerFactory.getLogger(OidcLoginFilter.class);

    private static final Scope SCOPE = new Scope("openid", "profile", "email");

    private static final List<String> EXCLUDED_PREFIXES = List.of(
            "/angular/",
            "/ckeditor/",
            "/favicon.ico",
            "/logging/",
            "/navapp-assets/",
            "/skin/",
            "/ping/",
            "/resetpassword",
            "/sso/",
            "/wicket/",
            "/ws/redirects"
    );

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/ws/navigationitems",
            "/ws/indexexport"
    );

    private SsoConfig ssoConfig;
    private OidcConfig oidcConfig;
    private boolean configured = false;

    private synchronized void ensureConfigured() throws ServletException {
        if (configured) {
            return;
        }
        this.ssoConfig = SsoConfig.get();

        // Early return if SSO is not enabled
        if (SsoConfig.Mode.OFF == this.ssoConfig.mode()) {
            this.configured = true;
            return;
        }

        try {
            this.oidcConfig = OidcConfig.get();
        } catch (Exception ex) {
            throw new ServletException("Not configured", ex);
        }
        this.configured = true;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ensureConfigured();

        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        String requestUrl = queryString == null ? requestUri : requestUri + "?" + queryString;
        LOG.debug("OIDCLoginFilter - {} {}", request.getMethod(), requestUrl);

        // Only redirect GET requests to the IdP. Redirecting other requests
        // would cause the request body to be lost.
        if (!"GET".equals(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (!requireSsoSession(request)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        Object creds = session.getAttribute(CREDENTIALS_ATTR_NAME);
        if (creds != null) {
            request.setAttribute(CREDENTIALS_ATTR_NAME, creds);
            chain.doFilter(request, response);
            return;
        }

        String url = idpRedirect(session);
        LOG.info("Redirecting from {}", requestUrl);
        LOG.info("Redirecting to {}", url);
        session.setAttribute(SsoSessionAttributes.RETURN_URL, requestUrl);
        response.sendRedirect(url);
    }

    private boolean requireSsoSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean sessionAttr = session != null && session.getAttribute(SsoSessionAttributes.SSO) != null;
        boolean sso = switch (ssoConfig.mode()) {
            case REQUIRED -> true;
            case OPTIONAL -> sessionAttr || cookiePreference(request, this.ssoConfig.enabled());
            case OFF -> false;
        };
        return sso && !isExcluded(request) && !isLoggedOut(request);
    }

    private static boolean isLoggedOut(HttpServletRequest request) {
        return request.getParameter("loginmessage") != null;
    }

    private static boolean isExcluded(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String path = requestURI.substring(contextPath.length());
        return EXCLUDED_PREFIXES.stream().anyMatch(path::startsWith)
                || EXCLUDED_PATHS.contains(path);
    }

    private boolean cookiePreference(HttpServletRequest request, SsoConfig.Default enabled) {
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)) {
            // Early return if no cookies
            return enabled == SsoConfig.Default.ON;
        }
        Optional<Boolean> first = Arrays.stream(cookies)
                .filter(c -> SsoFilter.SSO_COOKIE_NAME.equalsIgnoreCase(c.getName()))
                .map(Cookie::getValue)
                .map(BooleanUtils::toBoolean)
                .findFirst();
        return first.orElse(enabled == SsoConfig.Default.ON);
    }

    private String idpRedirect(HttpSession session) {
        State state = new State();
        Nonce nonce = new Nonce();
        CodeVerifier codeVerifier = new CodeVerifier();
        session.setAttribute(SsoSessionAttributes.CODE_VERIFIER, codeVerifier);
        session.setAttribute(SsoSessionAttributes.STATE, state);
        session.setAttribute(SsoSessionAttributes.NONCE, nonce);

        AuthenticationRequest authRequest = new AuthenticationRequest.Builder(
                new ResponseType(ResponseType.Value.CODE),
                SCOPE,
                oidcConfig.clientId(),
                URI.create(oidcConfig.redirectUri()))
                .endpointURI(oidcConfig.authorizationEndpoint())
                .codeChallenge(codeVerifier, CodeChallengeMethod.S256)
                .state(state)
                .responseType(ResponseType.CODE)
                .nonce(nonce)
                .build();

        return authRequest.toURI().toString();
    }

}
