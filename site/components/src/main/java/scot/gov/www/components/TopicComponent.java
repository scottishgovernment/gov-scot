package scot.gov.www.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.FilterException;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.BaseFilter;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scot.gov.www.beans.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class

TopicComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(TopicComponent.class);

    private static final String PUBLICATIONTYPE = "govscot:publicationType";

    private static final String DISPLAY_DATE = "govscot:displayDate";

    private static final String TITLE = "govscot:title";

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        super.doBeforeRender(request, response);

        HstRequestContext context = request.getRequestContext();
        HippoBean base = context.getSiteContentBaseBean();
        Topic topic = context.getContentBean(Topic.class);
        if (topic == null) {
            pageNotFound(response);
            return;
        }
        request.setAttribute("document", topic);
        populatePoliciesAndDirectorates(base, topic, request);
        populateNews(base, topic, request);
        populateConsultations(base, topic, request);
        populatePublications(base, topic, request);
        populateStatsAndResearch(base, topic, request);
    }

    private void populatePoliciesAndDirectorates(HippoBean base, Topic topic, HstRequest request) {
        try {
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(topic, base, "*/@hippo:docbase", Policy.class, false);
            query.addOrderByAscending(TITLE);
            HippoBeanIterator policies = executeQueryLoggingException(query, request, "policies");
            populateDirectorates(policies, request);
        } catch (QueryException e) {
            LOG.warn("Unable to get policies for topic {}", topic.getPath(), e);
        }
    }

    private void populateDirectorates(HippoBeanIterator policies, HstRequest request) {
        // populate the directorates responsible for policies into a map - this will
        // remove any duplicates
        Map<String, HippoBean> directoratesById = new HashMap<>();
        while (policies.hasNext()) {
            Policy policy = (Policy) policies.nextHippoBean();
            if (policy.getResponsibleDirectorate() != null) {
                directoratesById.put(policy.getResponsibleDirectorate().getIdentifier(),
                        policy.getResponsibleDirectorate());
            }
            for (HippoBean directorate : policy.getSecondaryResponsibleDirectorate()) {
                directoratesById.put(directorate.getIdentifier(), directorate);
            }
        }

        // now add them to a list and sort them by name
        List<HippoBean> directorates = directoratesById.values().stream()
                .sorted(comparing(bean -> bean.getSingleProperty(TITLE))).collect(toList());
        request.setAttribute("directorates", directorates);
    }

    private void populateNews(HippoBean base, Topic topic, HstRequest request) {
        HstQuery query = topicLinkedBeansQuery(topic, base, News.class, "govscot:publicationDate");

        try {
            executeQueryLoggingException(query, request, "news");
        } catch (QueryException e) {
            LOG.warn("Unable to get News for topic {}", topic.getPath(), e);
        }
    }

    private void populateConsultations(HippoBean base, Topic topic, HstRequest request) {
        HstQuery query = topicLinkedBeansQuery(topic, base, Publication.class, DISPLAY_DATE);
        if (query == null) {
            return;
        }

        try {
            Filter consultationPaperFilter = query.createFilter();
            consultationPaperFilter.addContains(PUBLICATIONTYPE, "consultation-");

            BaseFilter topicFilter = query.getFilter();
            query.setFilter(consultationPaperFilter);
            consultationPaperFilter.addAndFilter(topicFilter);

            executeQueryLoggingException(query, request, "consultations");
        } catch (QueryException e) {
            LOG.error("Unable to get Consultations for topic {}", topic.getPath(), e);
        }
    }

    void populatePublications(HippoBean base, Topic topic, HstRequest request) {
        HstQuery query = topicLinkedBeansQuery(topic, base, Publication.class, DISPLAY_DATE);
        if (query == null) {
            return;
        }

        try {
            Filter consultationFilter = query.createFilter();
            consultationFilter.addNotContains(PUBLICATIONTYPE, "consultation-paper");
            consultationFilter.addNotContains(PUBLICATIONTYPE, "consultation-analysis");

            BaseFilter topicFilter = query.getFilter();
            query.setFilter(consultationFilter);
            consultationFilter.addAndFilter(topicFilter);

            executeQueryLoggingException(query, request, "publications");
        } catch (QueryException e) {
            LOG.error("Unable to get Publications for topic {}", topic.getPath(), e);
        }
    }

    private void populateStatsAndResearch(HippoBean base, Topic topic, HstRequest request) {

        HstQuery query = topicLinkedBeansQuery(topic, base, Publication.class, DISPLAY_DATE);
        if (query == null) {
            return;
        }
        try {
            Filter typesFilter = equalsOneOfTheseTypesFilter(query, "statistics", "research-and-analysis");
            BaseFilter topicFilter = query.getFilter();
            query.setFilter(typesFilter);
            typesFilter.addAndFilter(topicFilter);
            executeQueryLoggingException(query, request, "statsAndResearch");

        } catch (QueryException e) {
            LOG.error("Unable to get Consultations for topic {}", topic.getPath(), e);
        }
    }

    private Filter equalsOneOfTheseTypesFilter(HstQuery query, String ...types) throws FilterException {
        Filter filter = query.createFilter();

        Filter sub = query.createFilter();
        for (String type : types) {
            Filter typeFilter = query.createFilter();
            typeFilter.addContains(PUBLICATIONTYPE, type);
            sub.addOrFilter(typeFilter);
        }
        filter.addAndFilter(sub);
        return filter;
    }

    private HstQuery topicLinkedBeansQuery(Topic topic, HippoBean base, Class linkedClass, String dateField) {
        try {
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    topic,
                    base,
                    "*/@hippo:docbase",
                    linkedClass,
                    true);
            query.addOrderByDescending(dateField);
            query.addOrderByAscending(TITLE);
            query.setLimit(3);
            return query;
        } catch (QueryException e) {
            LOG.warn("Unable to get linked beans for topic {}", topic.getPath(), e);
            return null;
        }
    }

    private HippoBeanIterator executeQueryLoggingException(
            HstQuery query,
            HstRequest request,
            String name)
            throws QueryException {
        HstQueryResult result = query.execute();
        request.setAttribute(name, result.getHippoBeans());
        return result.getHippoBeans();
    }

}