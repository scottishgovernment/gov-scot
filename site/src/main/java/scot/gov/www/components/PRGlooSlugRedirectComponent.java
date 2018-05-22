package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
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
import scot.gov.www.beans.News;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;

/**
 * redirect prgloo slugs
 */
public class PRGlooSlugRedirectComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DirectorateComponent.class);

    private static final String ARCHIVE_TEMPLATE = "https://www.webarchive.org.uk/wayback/archive/3000/http://news.gov.scot/%s";

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        String slug = lastPathElement(request);

        // see if the slug is the pr gloo slug of a news item we have imported
        HippoBean bean = findBySlug(slug, request);
        if (bean != null) {
            HstRequestContext context = request.getRequestContext();
            final HstLink link = context.getHstLinkCreator().create(bean, context);
            HstResponseUtils.sendPermanentRedirect(request, response, link.getPath());
            return;
        }

        // see if the slug is a know prgloo slug that we have not imported
        // if so then send a redirect to the archive.
        if (isArchivedSlug(slug, request)) {
            String archiveUrl = String.format(ARCHIVE_TEMPLATE, slug);
            LOG.info("Redirecting slug {} to archive: {}", slug, archiveUrl);
            HstResponseUtils.sendPermanentRedirect(request, response, archiveUrl);
            return;
        }

        // we do not know this slug, send a 404
        response.setStatus(404);
    }

    private boolean isArchivedSlug(String slug, HstRequest request)  {
        String query = String.format("/jcr:root/content/redirects/prgloo/*[govscot:slug = '%s']", slug);
        try {
            QueryResult result = request
                    .getRequestContext()
                    .getSession()
                    .getWorkspace()
                    .getQueryManager()
                    .createQuery(query, Query.XPATH)
                    .execute();
            NodeIterator nodeIt = result.getNodes();
            return nodeIt.getSize() > 0;
        } catch (RepositoryException e) {
            LOG.error("Failed to query for archived prgloo slug {}", slug, e);
            return false;
        }
    }

    private HippoBean findBySlug(String slug, HstRequest request) {
        HstQuery query = HstQueryBuilder
                .create(request.getRequestContext().getSiteContentBaseBean())
                .ofTypes(News.class)
                .where(constraint("govscot:prglooslug").equalTo(slug))
                .build();
        return executeQuery(query, slug);
    }

    private String lastPathElement(HstRequest request) {
        String [] pathElements = request.getPathInfo().split("/");
        return pathElements[pathElements.length - 1];
    }

    private HippoBean executeQuery(HstQuery query, String slug) {
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
}
