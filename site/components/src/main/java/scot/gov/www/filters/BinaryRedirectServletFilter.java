package scot.gov.www.filters;

import org.hippoecm.hst.servlet.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.apache.commons.lang3.StringUtils.*;
import static scot.gov.www.components.RedirectComponent.GOVSCOT_URL;

/**
 * Intercept binary requests to understand legacy binary urls.
 *
 * When publications were migrated from Rubric Non-APS documents were stored using a guid for their handle and
 * resource names because this is how they were stroed in amphora / doctor.  We have since migrated these to use the
 * slug of the filename.  This servlet filter provides redirects for these moved resources.
 *
 * This is an example if such a url:
 * /binaries/content/documents/govscot/publications/form/2015/10/community-right-to-buy-application-form-and-guidance/documents/605a96e1-81c3-4f61-869c-788437a024fb/605a96e1-81c3-4f61-869c-788437a024fb/govscot%3Adocument?
 *
 */
public class BinaryRedirectServletFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryRedirectServletFilter.class);

    static final String X_FORWARDED_HOST = "x-forwarded-host";

    static final String X_FORWARDED_PROTO = "x-forwarded-proto";

    // provide a session.  overridable in unit tests
    SessionProvider sessionProvider = request -> SessionUtils.getBinariesSession(request);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing required
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        //servletRequest.getPath
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String newPath = findRedirectPath(httpServletRequest);

        if (newPath != null) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            String url = getUrl(httpServletRequest, newPath);
            LOG.info("binary redirect {} -> {}", httpServletRequest.getPathInfo(), url);
            httpServletResponse.sendRedirect(url);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    /**
     * Get the url that this path should redirect to.
     *
     * If the x-forwarded-host and x-forwarded-proto headers are set then use them to construct a full url. Otherwise
     * use the context path and redirect path.
     */
    private String getUrl(HttpServletRequest req, String newPath) {
        if (hasForwardingHeaders(req)) {
            String host = req.getHeader(X_FORWARDED_HOST);
            String proto = req.getHeader(X_FORWARDED_PROTO);
            return String.format("%s://%s%s", proto, host, newPath);
        } else {
            return String.format("%s%s", req.getContextPath(), newPath);
        }
    }

    private boolean hasForwardingHeaders(HttpServletRequest req) {
        return isNoneBlank(req.getHeader(X_FORWARDED_HOST), req.getHeader(X_FORWARDED_PROTO));
    }

    private String findRedirectPath(HttpServletRequest request) throws IOException {
        // get a session
        Session session = null;
        try {
            session = sessionProvider.get(request);

            String redirectPath = jcrLookupPath(request);
            if (!session.nodeExists(redirectPath)) {
                return null;
            }

            Node node = session.getNode(redirectPath);
            if (!node.hasProperty(GOVSCOT_URL)) {
                return null;
            }
            return node.getProperty(GOVSCOT_URL).getString();
        } catch (RepositoryException e) {
            LOG.warn("RepositoryException while getting stream for binaries request '{}'. {}", request.getRequestURI(), e);
            return null;
        } finally {
            SessionUtils.releaseSession(request, session);
        }
    }

    static String jcrLookupPath(HttpServletRequest request) throws UnsupportedEncodingException {
        String characterEncoding = request.getCharacterEncoding();
        if (characterEncoding == null) {
            characterEncoding = "ISO-8859-1";
        }
        String undecoded = String.format("/content/redirects/Aliases/binaries%s", request.getPathInfo());
        String decoded = URLDecoder.decode(undecoded, characterEncoding);
        return decoded.replaceAll("govscot:document", "govscot%3Adocument");
    }

    @Override
    public void destroy() {
        // nothing required
    }

    /**
     * Session provider interface - this enables us to iverride how we get a sesison in unit tests since Hippo use a static
     * method.
     */
    interface SessionProvider {
        Session get(HttpServletRequest request) throws RepositoryException;
    }

}
