package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.Redirect;
import scot.gov.publishing.hippo.redirects.hst.AliasRedirectService;

import javax.jcr.Session;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Component used to support the redirect behaviour of gov.scot: if the sitemap does not match any other entry then this
 * component will check if a redirect id available before sending a 404.
 *
 * Redirects are checked in the following order:
 * 1 - url aliases, stored under /content/redirects/Aliases/<requestpath>  These are used to provide point to point redirects
 *     many of which came from Rubric url aliases
 *
 * 2 - if it is an old style publicaitons url (i.e. starts with /Publications/, then check imported publications and pages
 *     to see if the importer has recorded this url.
 *     Publication urls are stored in govscot:Publication and govscot:PublicationPage nodes.
 *
 * 3 - check to see if this is a historical url that can be sent to the archive / www2.gov,scot.
 *     Stored under /content/redirects/HistoricalUrls
 *
 */
public class RedirectComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectComponent.class);

    public static final String GOVSCOT_URL = "govscot:url";

    AliasRedirectService aliasRedirectService = new AliasRedirectService();

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {

        // check if this url is a known url alias
        String url = findAlias(request);
        if (url != null) {
            LOG.info("Redirecting to url alias {} -> {}", request.getPathInfo(), url);
            response.setStatus(301);
            response.setHeader("Location", url);
            return;
        }

        // we do not know this url, send a 404
        HstRequestContext context = request.getRequestContext();
        HippoBean document = context.getContentBean();
        request.setAttribute("document", document);
        request.setAttribute("isPageNotFound", true);
        LOG.info("404 for {}", request.getRequestURL());
        response.setStatus(404);
    }

    private String findAlias(HstRequest request) {
        try {
            Session session = request.getRequestContext().getSession();
            Optional<Redirect> redirect = aliasRedirectService.lookup(session, request.getPathInfo());
            if (redirect.isEmpty() || redirect.get().isHistoricalUrl()) {
                return null;
            }
            String url = redirect.get().getTo();
            // if the incoming url has parameters and the outgoing one does not then pass them on
            if (!url.contains("?") && isNotBlank(request.getQueryString())) {
                url = url + '?' + request.getQueryString();
            }
            return url;
        } catch (Exception e) {
            LOG.error("Failed to find url alias {}", request.getPathInfo(), e);
            return null;
        }
    }
}

