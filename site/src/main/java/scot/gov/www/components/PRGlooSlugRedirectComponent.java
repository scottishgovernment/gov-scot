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
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.HstResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.News;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;

/**
 * redirect prgloo slugs
 */
public class PRGlooSlugRedirectComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DirectorateComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        HstRequestContext context = request.getRequestContext();
        HippoBean bean = findBySlug(request);
        if (bean != null) {
            HstLinkCreator linkCreator = context.getHstLinkCreator();
            final HstLink link = linkCreator.create(bean, context);
            HstResponseUtils.sendPermanentRedirect(request, response, link.getPath());
            return;
        }

        response.setStatus(404);
    }

    private HippoBean findBySlug(HstRequest request) {
        HippoBean scope = request.getRequestContext().getSiteContentBaseBean();
        String slug = lastPathElement(request);
        HstQuery query = HstQueryBuilder
                .create(scope)
                .ofTypes(News.class)
                .where(constraint("govscot:prglooslug").equalTo(slug))
                .build();
        return executeQuery(query, slug);
    }

    private String lastPathElement(HstRequest request) {
        String pathElements [] = request.getPathInfo().split("/");
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
