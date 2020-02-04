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
import scot.gov.www.beans.Publication;
import scot.gov.www.beans.PublicationPage;
import scot.gov.www.beans.SimpleContent;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static scot.gov.www.components.ArchiveUtils.isArchivedUrl;

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

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {

        // check if this url is a known url alias
        String url = findAlias(request);
        if (url != null) {
            LOG.info("Redirecting to url alias {} -> {}", request.getPathInfo(), url);
            HstResponseUtils.sendPermanentRedirect(request, response, url);
            return;
        }

        // if this is a publications url then check if we have a publication for it
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
        response.setStatus(404);
    }

    private String findAlias(HstRequest request)  {
        try {
            Session session = request.getRequestContext().getSession();
            String path = String.format("/content/redirects/Aliases%s", ArchiveUtils.escapeJcrPath(request.getPathInfo()));
            if (session.nodeExists(path)) {
                Node node = session.getNode(path);
                if (node.hasProperty(GOVSCOT_URL)) {
                    return node.getProperty(GOVSCOT_URL).getString();
                }
            }
            return null;
        } catch (RepositoryException e) {
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

