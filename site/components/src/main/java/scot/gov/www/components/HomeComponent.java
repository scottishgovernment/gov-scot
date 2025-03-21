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
import scot.gov.www.beans.Topic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;

public class HomeComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DirectorateComponent.class);
    private static final String PUBLICATIONTYPE = "govscot:publicationType";
    private static final String PUBLICATIONS = "publications";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        request.setAttribute("isHomepage", true);
        HstRequestContext context = request.getRequestContext();
        HippoBean scope = context.getSiteContentBaseBean();
        populateNews(scope.getBean("news"), request);
        populateStatsAndResearch(scope.getBean(PUBLICATIONS), request);
        populateConsultations(scope.getBean(PUBLICATIONS), request);
        populatePublications(scope.getBean(PUBLICATIONS), request);
        populateTopicsList(scope.getBean("topics"), request);
        request.setAttribute("firstMinister", scope.getBean("about/who-runs-government/first-minister/index"));
        request.setAttribute("document", context.getContentBean());
    }

    private void populateNews(HippoBean scope, HstRequest request) {
        HstQuery query = HstQueryBuilder.create(scope)
                .ofTypes(News.class)
                .limit(3)
                .orderByDescending("govscot:publicationDate")
                .orderByAscending("govscot:title").build();
        executeQueryLoggingException(query, request, "news");
    }

    static void populateStatsAndResearch(HippoBean scope, HstRequest request) {
        HstQuery query = publicationsQuery(scope)
                .where(
                        or(
                                constraint(PUBLICATIONTYPE).equalTo("research-and-analysis"),
                                constraint(PUBLICATIONTYPE).equalTo("statistics")
                        )
                )
                .build();
        executeQueryLoggingException(query, request, "statisticsAndResearch");
    }


    private void populateConsultations(HippoBean scope, HstRequest request) {
        HstQuery query = publicationsQuery(scope)
                .where(
                        or(
                                constraint(PUBLICATIONTYPE).equalTo("consultation-paper"),
                                constraint(PUBLICATIONTYPE).equalTo("consultation-analysis")
                        )
                )
                .build();
        executeQueryLoggingException(query, request, "consultations");
    }

    static void populatePublications(HippoBean scope, HstRequest request) {
        HstQuery query = publicationsQuery(scope)
                .where(
                        and(
                                constraint(PUBLICATIONTYPE).notEqualTo("consultation-paper"),
                                constraint(PUBLICATIONTYPE).notEqualTo("consultation-analysis")
                        )
                )
                .build();
        executeQueryLoggingException(query, request, PUBLICATIONS);
    }


    private void populateTopicsList(HippoBean scope, HstRequest request) {
        List<Topic> topics = scope.getChildBeans(Topic.class);
        Comparator<Topic> titleComparator = Comparator.comparing(Topic::getTitle);
                Collections.sort(topics, titleComparator);
        request.setAttribute("topics", topics);
    }


    static HstQueryBuilder publicationsQuery(HippoBean scope) {
        return HstQueryBuilder.create(scope)
                .ofTypes(Publication.class)
                .limit(3)
                .orderByDescending("govscot:displayDate")
                .orderByDescending("govscot:publicationDate")
                .orderByDescending("hippostdpubwf:lastModificationDate")
                .orderByAscending("govscot:title");
    }

    static void executeQueryLoggingException(HstQuery query, HstRequest request, String name) {
        try {
            HstQueryResult result = query.execute();
            LOG.debug("executeQueryLoggingException {}, {}", name, result.getSize());

            request.setAttribute(name, result.getHippoBeans());
        } catch (QueryException e) {
            LOG.error("Failed to get {}", name, e);
        }
    }
}
