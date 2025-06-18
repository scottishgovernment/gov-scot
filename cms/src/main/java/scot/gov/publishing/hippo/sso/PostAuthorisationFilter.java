package scot.gov.publishing.hippo.sso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hippoecm.frontend.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

import javax.jcr.SimpleCredentials;
import java.io.IOException;

public class PostAuthorisationFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(PostAuthorisationFilter.class);

    private static final String SSO_USER_STATE = SSOUserState.class.getName();

    private static final ThreadLocal<SSOUserState> userStateHolder = new ThreadLocal<SSOUserState>();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            log.debug("User not authenticated");
            chain.doFilter(request, response);
            return;
        }

        // Check if the user already has a SSO user state stored in HttpSession before.
        HttpSession session = request.getSession();
        SSOUserState userState = (SSOUserState) session.getAttribute(SSO_USER_STATE);
        if (userState == null || !userState.getSessionId().equals(session.getId())) {
            userState = createSessionAttribute(session);
        }

        // If the user has a valid SSO user state, then
        // set a JCR Credentials as request attribute (named by FQCN of UserCredentials class).
        // Then the CMS application will use the JCR credentials passed through this request attribute.
        if (userState != null && userState.getSessionId().equals(session.getId())) {
            request.setAttribute(UserCredentials.class.getName(), userState.getCredentials());
        }

        try {
            userStateHolder.set(userState);
            logRequest(request, userState);
            chain.doFilter(request, response);
        } finally {
            userStateHolder.remove();
        }
    }

    private SSOUserState createSessionAttribute(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user already has a SSO user state stored in HttpSession before.
        SSOUserState userState = (SSOUserState) session.getAttribute(SSO_USER_STATE);
        if (userState != null && userState.getSessionId().equals(session.getId())) {
            return userState;
        }

        String username = extractUserName(authentication);
        if (username == null) {
            log.warn("No name found in SAML response");
            return null;
        }

        // Bloomreach requires the password in credentials must be non-empty array.
        char[] password = {0};
        SimpleCredentials credentials = new SimpleCredentials(username, password);
        credentials.setAttribute(SSOUserState.SAML_ID, username);
        userState = new SSOUserState(new UserCredentials(credentials), session.getId());
        session.setAttribute(SSO_USER_STATE, userState);
        return userState;
    }

    private String extractUserName(Authentication authentication) {
        if (!(authentication instanceof Saml2Authentication)) {
            return authentication.getName();
        }
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
        return principal.getFirstAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress");
    }

    private static void logRequest(HttpServletRequest req, SSOUserState sso) {
        String username = "<anonymous>";
        if (sso != null) {
            username = sso.getCredentials().getUsername();
        }
        StringBuffer url = req.getRequestURL();
        if (req.getQueryString() != null) {
            url.append('?').append(req.getQueryString());
        }
        log.info("doFilter({}) LoginSuccessFilter {}", username, url);
    }

    /**
     * Get current <code>SSOUserState</code> instance from the current thread local context.
     */
    static SSOUserState getCurrentSSOUserState() {
        return userStateHolder.get();
    }

}
