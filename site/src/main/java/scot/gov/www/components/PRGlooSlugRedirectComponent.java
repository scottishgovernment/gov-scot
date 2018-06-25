package scot.gov.www.components;

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
import scot.gov.www.beans.News;

import javax.jcr.RepositoryException;

import java.io.IOException;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;

/**
 * Redirect prgloo slugs
 *
 * This component will first try to match the slug against the prglooslug property of News articles in the repo.
 * If a match is found a redirect will be issues to that item.
 *
 * If not then the slug will then be matched against the list of all historical prgloo slugs contained in the
 * repo under /content/redirects/prgloo/ if found a redirect to the achive will be issued.
 *
 * Finally if it not found then a 404 will be sent.
 */
public class PRGlooSlugRedirectComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PRGlooSlugRedirectComponent.class);

    private static final String ARCHIVE_TEMPLATE = "https://www.webarchive.org.uk/wayback/archive/3000/news.gov.scot/news/%s";

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        String slug = lastPathElement(request);

        // see if the slug is the prgloo slug of a news item we have imported to the repo
        HippoBean bean = findByPRGlooSlug(slug, request);
        if (bean != null) {
            HstRequestContext context = request.getRequestContext();
            final HstLink link = context.getHstLinkCreator().create(bean, context);
            HstResponseUtils.sendPermanentRedirect(request, response, link.getPath());
            return;
        }

        // see if the slug is a known prgloo slug that we have not imported
        // if so then send a redirect to the archive.
        if (isArchivedSlug(slug, request)) {
            String archiveUrl = String.format(ARCHIVE_TEMPLATE, slug);
            LOG.info("Redirecting slug {} to archive: {}", slug, archiveUrl);
            HstResponseUtils.sendPermanentRedirect(request, response, archiveUrl);
            return;
        }

        // we do not know this slug, send a 404
        try {
            response.setStatus(404);
            response.forward("/pagenotfound");
            return;
        }  catch (IOException e) {
            throw new HstComponentException("forward failed", e);
        }
    }

    private String lastPathElement(HstRequest request) {
        String [] pathElements = request.getPathInfo().split("/");
        return pathElements[pathElements.length - 1];
    }

    private HippoBean findByPRGlooSlug(String slug, HstRequest request) {
        HstQuery query = HstQueryBuilder
                .create(request.getRequestContext().getSiteContentBaseBean())
                .ofTypes(News.class)
                .where(constraint("govscot:prglooslug").equalTo(slug))
                .build();
        return executeHstQuery(query, slug);
    }

    private HippoBean executeHstQuery(HstQuery query, String slug) {
        try {
            HstQueryResult result = query.execute();
            if (result.getTotalSize() == 0) {
                LOG.warn("PRGloo slug not found: {}", slug);
                return null;
            }

            if (result.getTotalSize() > 1) {
                LOG.warn("Multiple news items with this slug : {}, will use first", slug);
            }

            return result.getHippoBeans().nextHippoBean();
        } catch (QueryException e) {
            LOG.error("Failed to get news by prgloo slug {}", slug, e);
            return null;
        }
    }

    private boolean isArchivedSlug(String slug, HstRequest request) {
        // form the letters of the slug into a path e.g. myslug -> /m/y/s/l/u/g/
        StringBuffer path = new StringBuffer("/content/redirects/");
        for(char c : slug.toCharArray()) {
            path.append(c).append("/");
        }

        try {
            return request.getRequestContext().getSession().nodeExists(path.toString());
        } catch (RepositoryException e) {
            LOG.warn("Failed to determine existance of slug {}", slug, e);
            return false;
        }
    }

}
