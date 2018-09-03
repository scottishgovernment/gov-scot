package scot.gov.www.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.HstResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Publication;
import scot.gov.www.beans.PublicationPage;

import java.io.IOException;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static scot.gov.www.components.ArchiveUtils.isArchivedUrl;
import static scot.gov.www.components.ArchiveUtils.sendArchiveRedirect;

/**
 * Redirect old gov.scot style publication urls.
 *
 * All imported publications and publications pages have their gov.scot url recorded in the govscot:govscoturl
 * attribute.  Whan a /Publications/* url is requested we first look to see if it has been imported.  When doing
 * this we disregard any trailing /downloads element since in the new site does not have a seprate downloads page.
 *
 * If the url cannot be resolved as an imported publication or publication page then we next check under
 * /content/redirects/Publications to see if the url is a known publication url.  If it is then we redirect the
 * url to the archive url.  Currently this is the live www.gov.scot site but this will change.
 *
 * Finally if there is no match the send a 404 and show the 404 page.
 */
public class PublicationsRedirectComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsRedirectComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {

        // see if this url has been recorded as the url for any imported publication
        HippoBean bean = findByGovScotUrl(request);
        if (bean != null) {
            HstRequestContext context = request.getRequestContext();
            final HstLink link = context.getHstLinkCreator().create(bean, context);
            LOG.info("Redirecting govscot publicaiton url {} to {}", request.getPathInfo(), link.getPath());
            HstResponseUtils.sendPermanentRedirect(request, response, link.getPath());
            return;
        }

        // check if this url is a known publications url
        if (isArchivedUrl(request)) {
            sendArchiveRedirect(request.getPathInfo(), request, response);
            return;
        }

        // we do not know this publications, send a 404
        try {
            response.setStatus(404);
            response.forward("/pagenotfound");
            return;
        }  catch (IOException e) {
            throw new HstComponentException("forward failed", e);
        }
    }

    private HippoBean findByGovScotUrl(HstRequest request) {
        String govscotUrl = String.format("https://www.gov.scot%s", request.getPathInfo());

        // if the url ends with /downloads then remove it since there is no downloads page in the new version
        // of publications
        if (govscotUrl.endsWith("/downloads")) {
            govscotUrl = StringUtils.substringBeforeLast(govscotUrl, "/downloads");
        }

        HstQuery query = HstQueryBuilder
                .create(request.getRequestContext().getSiteContentBaseBean())
                .ofTypes(Publication.class, PublicationPage.class)
                .where(constraint("govscot:govscoturl").equalTo(govscotUrl))
                .build();

        try {
            HstQueryResult result = query.execute();
            if (result.getTotalSize() == 0) {
                return null;
            }

            if (result.getTotalSize() > 1) {
                LOG.warn("Multiple publications with this url: {}, will use first", govscotUrl);
            }

            return result.getHippoBeans().nextHippoBean();
        } catch (QueryException e) {
            LOG.error("Failed to get publication by govscotUrl slug {}", govscotUrl, e);
            return null;
        }
    }
}
