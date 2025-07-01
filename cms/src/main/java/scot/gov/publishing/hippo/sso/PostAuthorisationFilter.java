package scot.gov.publishing.hippo.sso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.hippoecm.frontend.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

import javax.jcr.SimpleCredentials;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.emptyList;

public class PostAuthorisationFilter extends HttpFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PostAuthorisationFilter.class);

    /**
     * The name of the request attribute used to store the Bloomreach {@link UserCredentials}.
     * When this attribute is set, Bloomreach will use the contained JCR credentials.
     * If that user has appropriate permissions, Bloomreach will not show the login prompt.
     * The attribute is used by {@link org.hippoecm.frontend.session.PluginUserSession}.
     */
    public static final String CREDENTIALS_ATTR_NAME = UserCredentials.class.getName();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // User will be unauthenticated or anonymous if security config does not require it
        if (!(auth instanceof Saml2Authentication authentication)) {
            LOG.debug("User is not SAML authenticated");
            logRequest(request, null);
            chain.doFilter(request, response);
            return;
        }

        // Check if the user already has a SSO user state stored in HttpSession before.
        HttpSession session = request.getSession(false);
        UserCredentials credentials = null;

        if (session != null) {
            credentials = (UserCredentials) session.getAttribute(CREDENTIALS_ATTR_NAME);
        }

        if (credentials == null) {
            credentials = toCmsCredentials(authentication);
        }

        if (session != null) {
            if (credentials != null) {
                session.setAttribute(CREDENTIALS_ATTR_NAME, credentials);
            } else {
                session.removeAttribute(CREDENTIALS_ATTR_NAME);
            }
        }

        // Add the credentials as a request attribute where Bloomreach will find them
        if (credentials != null) {
            request.setAttribute(CREDENTIALS_ATTR_NAME, credentials);
        }

        logRequest(request, credentials);
        chain.doFilter(request, response);
    }

    /**
     * Converts the Spring Security Authentication instance to Bloomreach UserCredentials.
     * The UserCredentials instance wraps a JCR {@link javax.jcr.Credentials} instance.
     * Attributes are then set on that instance in order to make them available to the
     * {@link SamlUserManager}.
     */
    private UserCredentials toCmsCredentials(Saml2Authentication authentication) {
        String username = username(authentication);
        if (StringUtils.isBlank(username)) {
            LOG.warn("No name found in SAML response");
            return null;
        }

        // Bloomreach HippoLoginModule requires the password be a non-empty array
        char[] password = {0};
        SimpleCredentials credentials = new SimpleCredentials(username, password);
        credentials.setAttribute(SsoAttributes.SAML_ID, username);

        List<String> groups = groups(authentication);
        credentials.setAttribute(SsoAttributes.SAML_GROUPS, groups);

        return new UserCredentials(credentials);
    }

    private String username(Saml2Authentication authentication) {
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
        return principal.getName();
//        return principal.getFirstAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress");
    }

    private List<String> groups(Saml2Authentication authentication) {
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
        List<String> groups = principal.getAttribute("http://schemas.auth0.com/groups");
        if (groups == null) {
            groups = emptyList();
        }
        return groups;
    }

    private static void logRequest(HttpServletRequest req, UserCredentials credentials) {
        String username = "<anonymous>";
        if (credentials != null) {
            username = credentials.getUsername();
        }
        String sessionId = "none";
        HttpSession session = req.getSession(false);
        boolean sso = true;
        if (session != null) {
            sessionId = req.getSession().getId();
            sso = session.getAttribute("sso") != null;
        }
        StringBuffer url = req.getRequestURL();
        if (req.getQueryString() != null) {
            url.append('?').append(req.getQueryString());
        }
        LOG.info("doFilter({} - {} - {}) {}", username, sessionId, sso, url);
    }

}
