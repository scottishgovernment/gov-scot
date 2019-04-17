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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing required
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //servletRequest.getPath
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String newPath = findRedirectPath(httpServletRequest);

        if (newPath != null) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            LOG.info("binary redirect {} -> {}", httpServletRequest.getPathInfo(), newPath);
            httpServletResponse.sendRedirect("/site/" + newPath);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String findRedirectPath(HttpServletRequest request) throws IOException {
        // get a session
        Session session = null;
        try {
            session = SessionUtils.getBinariesSession(request);

            String redirectPath = redirectPath(request);
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

    private String redirectPath(HttpServletRequest request) throws UnsupportedEncodingException {
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

}
