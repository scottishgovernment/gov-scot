package scot.gov.www.sitemap;

import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.hst.core.request.SiteMapItemHandlerConfiguration;
import org.hippoecm.hst.core.sitemapitemhandler.HstSiteMapItemHandler;
import org.hippoecm.hst.core.sitemapitemhandler.HstSiteMapItemHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            HttpServletResponse response)
            throws HstSiteMapItemHandlerException {

        if (request.getPathTranslated().endsWith("/")) {
            return resolvedSiteMapItem;
        }

        sendRedirect(request, response);

        // Stop further processing on this request
        return null;
    }

    private void sendRedirect(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder uri = new StringBuilder();
        uri.append(request.getContextPath());
        uri.append(request.getServletPath());
        uri.append(request.getPathTranslated());
        uri.append('/');
        if (request.getQueryString() != null) {
            uri.append('?').append(request.getQueryString());
        }
        String redirect = uri.toString();
        try {
            LOG.debug("Sending trailing slash redirect to {}", redirect);
            response.sendRedirect(redirect);
        } catch (IOException ex) {
            throw new HstSiteMapItemHandlerException(ex);
        }
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

}
