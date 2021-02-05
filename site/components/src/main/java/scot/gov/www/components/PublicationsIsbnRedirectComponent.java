package scot.gov.www.components;

import org.apache.commons.lang3.StringUtils;
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

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.io.IOException;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;

/**
 * Redirect isbn urls
 */
public class PublicationsIsbnRedirectComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsIsbnRedirectComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {

        String isbn = isbn(request);
        HstRequestContext context = request.getRequestContext();

        // first try to find the isbn in imported publications
        HippoBean bean = findByIsbn(isbn, context);

        if (bean != null) {
            final HstLink link = context.getHstLinkCreator().create(bean, context);
            LOG.info("Redirecting to ISBN {} -> {}", isbn, link.getPath());
            HstResponseUtils.sendPermanentRedirect(request, response, link.getPath());
            return;
        }

        // check to see if we recorded this isbn in the historical publication urls
        String historicalPublicationPath = findHistoricalPublicationUrl(isbn, context);
        if (StringUtils.isNotBlank(historicalPublicationPath)) {
            ArchiveUtils.redirectToOldSite(historicalPublicationPath, request, response);
            return;
        }

        // we were not able to resolve the isbn, send a 404
        try {
            LOG.info("404 for {}", request.getRequestURL());
            response.setStatus(404);
            response.forward("/pagenotfound");
            return;
        }  catch (IOException e) {
            throw new HstComponentException("forward failed", e);
        }
    }

    private String isbn(HstRequest request) {
        String [] pathElements = request.getPathInfo().split("/");
        String isbn = pathElements[pathElements.length - 1];
        return isbn.toLowerCase().replaceAll("\\s", "").replaceAll("-", "");
    }

    private HippoBean findByIsbn(String isbn, HstRequestContext requestContext) {
        HstQuery query = HstQueryBuilder
                .create(requestContext.getSiteContentBaseBean())
                .ofTypes(Publication.class)
                .where(constraint("govscot:isbn").equalTo(isbn))
                .build();
        return executeQuery(query, isbn);
    }

    private String findHistoricalPublicationUrl(String isbn, HstRequestContext requestContext) {
        try {
            Session session = requestContext.getSession();

            String query = String.format("SELECT * FROM nt:unstructured WHERE govscot:isbn = '%s'", isbn);

            QueryResult result = session
                    .getWorkspace()
                    .getQueryManager()
                    .createQuery(query, Query.SQL)
                    .execute();

            if (result.getNodes().getSize() == 0) {
                LOG.info("No publication found for isbn {}", isbn);
                return null;
            }

            if (result.getNodes().getSize() > 1) {
                LOG.warn("Multiple publications found with isbn {}, will use the first one", isbn);
            }

            return StringUtils.substringAfter(result.getNodes().nextNode().getPath(), "/content/redirects/HistoricalUrls");
        } catch (RepositoryException e) {
            LOG.error("Failed to query for isbn url {}", isbn, e);
            return null;
        }

    }

    private HippoBean executeQuery(HstQuery query, String isbn) {
        try {
            HstQueryResult result = query.execute();
            if (result.getTotalSize() == 0) {
                LOG.info("ISBN not found: {}", isbn);
                return null;
            }

            if (result.getTotalSize() > 1) {
                LOG.warn("Multiple publications with this ISBN : {}, will use first", isbn);
            }

            return result.getHippoBeans().nextHippoBean();
        } catch (QueryException e) {
            LOG.error("Failed to get publication by ISBN {}", isbn, e);
            return null;
        }
    }
}