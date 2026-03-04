package scot.gov.publishing.hippo.sso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class OidcLoginFilter extends HttpFilter {

    public static final String CREDENTIALS_ATTR_NAME = SsoSessionAttributes.CREDENTIALS;

    private static final Logger LOG = LoggerFactory.getLogger(OidcLoginFilter.class);

    private static final List<String> EXCLUDED_PREFIXES = List.of(
            "/angular/",
            "/ckeditor/",
            "/logging/",
            "/navapp-assets/",
            "/skin/",
            "/resetpassword",
            "/sso/",
            "/wicket/",
            "/ws/redirects"
    );

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/favicon.ico",
            "/ping/",
            "/ws/navigationitems",
            "/ws/indexexport"
    );

    SsoConfig ssoConfig;
    RedirectHandler redirectHandler;
    boolean configured = false;

    private synchronized void ensureConfigured() {
        if (configured) {
            return;
        }
        this.ssoConfig = SsoConfig.get();

        // Early return if SSO is not enabled
        if (SsoConfig.Mode.OFF == this.ssoConfig.mode()) {
            this.configured = true;
            return;
        }

        this.redirectHandler = new RedirectHandler(OidcConfig.get());
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

        // Always propagate credentials from session to request, regardless of redirect mode.
        // In REQUIRED+MANUAL and OPTIONAL+MANUAL the filter does not auto-redirect, but
        // CallbackHandler still stores credentials in a fresh session after IdP authentication.
        // Without this check those credentials would be silently dropped.
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            Object creds = existingSession.getAttribute(CREDENTIALS_ATTR_NAME);
            if (creds != null) {
                request.setAttribute(CREDENTIALS_ATTR_NAME, creds);
                chain.doFilter(request, response);
                return;
            }
        }

        if (!requiresIdpRedirect(request)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(SsoSessionAttributes.RETURN_URL, requestUrl);
        String url = redirectHandler.buildRedirectUrl(session);
        LOG.info("Redirecting from {}", requestUrl);
        LOG.info("Redirecting to {}", url);
        response.sendRedirect(url);
    }

    private boolean requiresIdpRedirect(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean sessionAttr = session != null && session.getAttribute(SsoSessionAttributes.SSO) != null;
        boolean sso = switch (ssoConfig.mode()) {
            case OFF -> false;
            case REQUIRED -> ssoConfig.redirect() == SsoConfig.Redirect.AUTO;
            case OPTIONAL -> sessionAttr || cookiePreference(request);
        };
        return sso && !isExcluded(request) && !isLoggedOut(request);
    }

    private static boolean isLoggedOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SsoSessionAttributes.LOGGED_OUT) != null;
    }

    private static boolean isExcluded(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String path = requestURI.substring(contextPath.length());
        return EXCLUDED_PREFIXES.stream().anyMatch(path::startsWith)
                || EXCLUDED_PATHS.contains(path);
    }

    private boolean cookiePreference(HttpServletRequest request) {
        boolean systemDefault = ssoConfig.redirect() == SsoConfig.Redirect.AUTO;
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)) {
            return systemDefault;
        }
        return Arrays.stream(cookies)
                .filter(c -> SsoFilter.SSO_COOKIE_NAME.equalsIgnoreCase(c.getName()))
                .map(Cookie::getValue)
                .map(BooleanUtils::toBoolean)
                .findFirst()
                .orElse(systemDefault);
    }

}
