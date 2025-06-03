package scot.gov.publishing.hippo.sso;

import org.hippoecm.frontend.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.jcr.SimpleCredentials;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

import java.io.IOException;

public class LoginSuccessFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoginSuccessFilter.class);

    private static final String SSO_USER_STATE = SSOUserState.class.getName();

    private static final ThreadLocal<SSOUserState> userStateHolder = new ThreadLocal<SSOUserState>();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logRequest((HttpServletRequest) request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) {
            log.debug("User not authenticated");
            chain.doFilter(request, response);
            return;
        }

        // Check if the user already has a SSO user state stored in HttpSession before.
        HttpSession session = ((HttpServletRequest) request).getSession();
        SSOUserState userState = (SSOUserState) session.getAttribute(SSO_USER_STATE);

        if (userState == null || !userState.getSessionId().equals(session.getId())) {
            try {
                String username = extractUserName(authentication);
                if (username == null) {
                    log.warn("name is null in SAML response");
                    chain.doFilter(request, response);
                    return;
                }
                // Bloomreach requires the password in credentials must be non-empty array.
                char[] password = {0};
                SimpleCredentials credentials = new SimpleCredentials(username, password);
                credentials.setAttribute(SSOUserState.SAML_ID, username);
                userState = new SSOUserState(new UserCredentials(credentials), session.getId());
                session.setAttribute(SSO_USER_STATE, userState);
            } catch (Exception e) {
                log.debug("Error authenticating with SAML", e);
                chain.doFilter(request, response);
                return;
            }
        }

        // If the user has a valid SSO user state, then
        // set a JCR Credentials as request attribute (named by FQCN of UserCredentials class).
        // Then the CMS application will use the JCR credentials passed through this request attribute.
        if (userState.getSessionId().equals(session.getId())) {
            request.setAttribute(UserCredentials.class.getName(), userState.getCredentials());
        }

        try {
            userStateHolder.set(userState);
            chain.doFilter(request, response);
        } finally {
            userStateHolder.remove();
        }

    }

    private static void logRequest(HttpServletRequest req) {
        StringBuffer str = req.getRequestURL();
        if (req.getQueryString() != null) {
            str.append('?').append(req.getQueryString());
        }
        log.info("doFilter LoginSuccessFilter {}", str);
    }

    private String extractUserName(Authentication authentication) {
        if (!(authentication instanceof Saml2Authentication)) {
            return authentication.getName();
        }
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
        return principal.getFirstAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress");
    }

    /**
     * Get current <code>SSOUserState</code> instance from the current thread local context.
     */
    static SSOUserState getCurrentSSOUserState() {
        return userStateHolder.get();
    }

    @Override
    public void destroy() {

    }

}
