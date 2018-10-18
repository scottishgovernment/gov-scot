package scot.gov.www.sitemap;

import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.hst.core.request.SiteMapItemHandlerConfiguration;
import org.hippoecm.hst.core.sitemapitemhandler.HstSiteMapItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TrailingSlashRedirect implements HstSiteMapItemHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TrailingSlashRedirect.class);


    @Override
    public void init(ServletContext servletContext, SiteMapItemHandlerConfiguration handlerConfig) {
        // Nothing to initialise
    }

    @Override
    public ResolvedSiteMapItem process(
            ResolvedSiteMapItem resolvedSiteMapItem,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (request.getPathTranslated().endsWith("/")) {
            return resolvedSiteMapItem;
        }

        String redirect = redirectTarget(request);
        LOG.debug("Sending trailing slash redirect to {}", redirect);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", redirect);

        // Stop further processing on this request
        return null;
    }

    private String redirectTarget(HttpServletRequest request) {
        StringBuilder url = new StringBuilder(request.getRequestURL());
        url.append('/');
        if (request.getQueryString() != null) {
            url.append('?').append(request.getQueryString());
        }
        return url.toString();
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

}
