package scot.gov.www.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.HstResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.Redirect;
import scot.gov.publishing.hippo.redirects.hst.AliasRedirectService;
import scot.gov.www.beans.Publication;
import scot.gov.www.beans.PublicationPage;

import javax.jcr.Session;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;

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
            // if the incoming url has url paramaters and the outgoing one does nto then pass them on...
            HstResponseUtils.sendPermanentRedirect(request, response, url);
            return;
        }

        // if this is a publications url then check if we have a publication for it

        ///  TODO: is this stiull wanted?  wouldnt it be in the redirects?
        ///  these are stiull getting traffic.  I think these are a problem, they should be moved to the url lookup structure
        if (isOldStylePublicationUrl(request)) {
            HippoBean bean = findPublicationsByGovScotUrl(request);
            if (bean != null) {
                HstRequestContext context = request.getRequestContext();
                final HstLink link = context.getHstLinkCreator().create(bean, context);
                LOG.info("Redirecting govscot publication url {} to {}", request.getPathInfo(), link.getPath());
                HstResponseUtils.sendPermanentRedirect(request, response, link.getPath());
                return;
            }
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

    private boolean isOldStylePublicationUrl(HstRequest request) {
        String govscotUrl = request.getPathInfo();
        return StringUtils.startsWith(govscotUrl, "/Publications/");

    }

    private HippoBean findPublicationsByGovScotUrl(HstRequest request) {
        String govscotUrl = request.getPathInfo();

        // remove any trailing slash since they are stored without a trailing slash
        govscotUrl = StringUtils.removeEnd(govscotUrl, "/");

        // if the url ends with /downloads then remove it since there is no downloads page in the new version
        // of publications
        if (govscotUrl.endsWith("/downloads")) {
            govscotUrl = StringUtils.substringBeforeLast(govscotUrl, "/downloads");
        }

        HstQuery query = HstQueryBuilder
                .create(request.getRequestContext().getSiteContentBaseBean())
                .ofTypes(Publication.class, PublicationPage.class)
                .where(urlConstraint(govscotUrl))
                .build();

        try {
            LOG.info("qqqq {}", query);
            HstQueryResult result = query.execute();
            if (result.getTotalSize() == 0) {
                return null;
            }

            return result.getHippoBeans().nextHippoBean();
        } catch (QueryException e) {
            LOG.error("Failed to get publication by govscotUrl slug {}", govscotUrl, e);
            return null;
        }
    }

    Constraint urlConstraint(String govscotUrl) {
        return constraint("govscot:govscoturl").equalTo(govscotUrl);
    }
}

