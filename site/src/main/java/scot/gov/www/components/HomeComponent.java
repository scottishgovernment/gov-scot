package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.ObjectBeanManager;
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

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        request.setAttribute("isHomepage", true);

        HstRequestContext context = request.getRequestContext();
        HippoBean scope = context.getSiteContentBaseBean();
        populateNews(scope.getBean("news/"), request);
        populateConsultations(scope.getBean("publications/"), request);
        populatePublications(scope.getBean("publications/"), request);
        populateTopicsList(scope.getBean("topics/"), request);

        // get the First Minister page
        ObjectBeanManager beanManager = context.getObjectBeanManager();
        try {
            Object firstMinister = beanManager.getObject("/content/documents/govscot/about/who-runs-government/first-minister/");
            request.setAttribute("firstMinister", firstMinister);
        } catch (ObjectBeanManagerException e) {
            LOG.warn("Unable to get First Minister details {}", e);
        }

        // get the Home page
        try {
            Object homeContent = beanManager.getObject("/content/documents/govscot/home/");
            request.setAttribute("document", homeContent);
        } catch (ObjectBeanManagerException e) {
            LOG.warn("Unable to get Home content item details {}", e);
        }
    }

    private void populateNews(HippoBean scope, HstRequest request) {
        HstQuery query = HstQueryBuilder.create(scope)
                .ofTypes(News.class).limit(3).orderByDescending("govscot:publicationDate").build();
        executeQueryLoggingException(query, request, "news");
    }

    private void populateConsultations(HippoBean scope, HstRequest request) {
        HstQuery query = publicationsQuery(scope)
                .where(
                        or(
                                constraint(PUBLICATIONTYPE).equalTo("consultation-paper"),
                                constraint(PUBLICATIONTYPE).equalTo("consultation-response")
                        )
                )
                .build();
        executeQueryLoggingException(query, request, "consultations");
    }

    private void populatePublications(HippoBean scope, HstRequest request) {
        HstQuery query = publicationsQuery(scope)
                .where(
                        and(
                                constraint(PUBLICATIONTYPE).notEqualTo("consultation-paper"),
                                constraint(PUBLICATIONTYPE).notEqualTo("consultation-response")
                        )
                )
                .build();
        executeQueryLoggingException(query, request, "publications");
    }

    private void populateTopicsList(HippoBean scope, HstRequest request) {
        List<Topic> topics = scope.getChildBeans(Topic.class);
        Comparator<Topic> titleComparator = Comparator.comparing(Topic::getTitle);
                Collections.sort(topics, titleComparator);
        request.setAttribute("topics", topics);
    }

    private HstQueryBuilder publicationsQuery(HippoBean scope) {
        return HstQueryBuilder.create(scope)
                .ofTypes(Publication.class)
                .limit(3)
                .orderByDescending("govscot:publicationDate");
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
