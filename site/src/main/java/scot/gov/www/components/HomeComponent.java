package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.News;
import scot.gov.www.beans.Publication;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;

public class HomeComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DirectorateComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        HstRequestContext context = request.getRequestContext();
        HippoBean scope = context.getSiteContentBaseBean();
        populateNews(scope, request);
        populateConsultations(scope, request);
        populatePublications(scope, request);
    }

    private void populateNews(HippoBean scope, HstRequest request) {
        HstQuery query = HstQueryBuilder.create(scope)
                .ofTypes(News.class).limit(3).orderByDescending("govscot:publishedDate").build();
        executeQueryLoggingException(query, request, "news");
    }

    private void populateConsultations(HippoBean scope, HstRequest request) {
        HstQuery query = publicationsQuery(scope)
                .where(constraint("govscot:publicationsType").equalTo("consultation"))
                .build();
        executeQueryLoggingException(query, request, "Latest Consultations");
    }

    private void populatePublications(HippoBean scope, HstRequest request) {
        HstQuery query = publicationsQuery(scope)
                .where(constraint("govscot:publicationsType").notEqualTo("consultation"))
                .build();
        executeQueryLoggingException(query, request, "consultations");
    }

    private HstQueryBuilder publicationsQuery(HippoBean scope) {
        return HstQueryBuilder.create(scope)
                .ofTypes(Publication.class)
                .limit(3)
                .orderByDescending("govscot:publishedDate");
    }

    private void executeQueryLoggingException(HstQuery query, HstRequest request, String name) {
        try {
            HstQueryResult result = query.execute();
            request.setAttribute(name, result.getHippoBeans());
        } catch (QueryException e) {
            LOG.error("Failed to get {}", name, e);
        }
    }
}