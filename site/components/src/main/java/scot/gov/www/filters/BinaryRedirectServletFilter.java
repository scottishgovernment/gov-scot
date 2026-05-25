package scot.gov.www.filters;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.hippoecm.hst.servlet.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.Redirect;
import scot.gov.publishing.hippo.redirects.hst.AliasRedirectService;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

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
 * <p>Delegates the redirect lookup to {@link AliasRedirectService}, which in turn uses
 * {@link scot.gov.publishing.hippo.redirects.SwitchingRedirectRepository} so that both the
 * legacy path-mirror store and the new hash-bucketed store are queried correctly.
 */
public class BinaryRedirectServletFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryRedirectServletFilter.class);

    static final String X_FORWARDED_HOST = "x-forwarded-host";

    static final String X_FORWARDED_PROTO = "x-forwarded-proto";

    // overridable in unit tests
    SessionProvider sessionProvider = SessionUtils::getBinariesSession;

    // overridable in unit tests
    AliasRedirectService aliasRedirectService = new AliasRedirectService();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing required
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

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
     * Returns the redirect target for this request path, or {@code null} if no redirect exists.
     */
    private String findRedirectPath(HttpServletRequest request) {
        if (request.getPathInfo() == null) {
            return null;
        }
        Session session = null;
        try {
            session = sessionProvider.get(request);
            LOG.info("findRedirectPath {}", request.getPathInfo());
            return aliasRedirectService.lookup(session, "/binaries" + request.getPathInfo())
                    .map(Redirect::getTo)
                    .filter(StringUtils::isNotBlank)
                    .orElse(null);
        } catch (RepositoryException e) {
            LOG.warn("RepositoryException while getting stream for binaries request '{}'. {}",
                    request.getRequestURI(), e);
            return null;
        } finally {
            SessionUtils.releaseSession(request, session);
        }
    }

    /**
     * Builds the redirect URL.  If {@code x-forwarded-host} / {@code x-forwarded-proto} headers
     * are present the full URL is constructed from them; otherwise the context path is prepended.
     * If {@code newPath} is itself an absolute URL it is used verbatim.
     */
    private String getUrl(HttpServletRequest req, String newPath) {
        if (isUrl(newPath)) {
            return newPath;
        }
        if (hasForwardingHeaders(req)) {
            String host = req.getHeader(X_FORWARDED_HOST);
            String proto = req.getHeader(X_FORWARDED_PROTO);
            return String.format("%s://%s%s", proto, host, newPath);
        } else {
            return String.format("%s%s", req.getContextPath(), newPath);
        }
    }

    boolean isUrl(String path) {
        return Strings.CS.startsWith(path, "https://") || Strings.CS.startsWith(path, "http://");
    }

    private boolean hasForwardingHeaders(HttpServletRequest req) {
        return isNoneBlank(req.getHeader(X_FORWARDED_HOST), req.getHeader(X_FORWARDED_PROTO));
    }

    @Override
    public void destroy() {
        // nothing required
    }

    /**
     * Session provider — allows tests to inject a mock session without a real JCR container.
     */
    interface SessionProvider {
        Session get(HttpServletRequest request) throws RepositoryException;
    }
}
