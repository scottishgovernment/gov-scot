package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.FilterException;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.*;

public class TopicComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(TopicComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        HstRequestContext context = request.getRequestContext();
        HippoBean base = context.getSiteContentBaseBean();
        Topic topic = context.getContentBean(Topic.class);
        request.setAttribute("document", topic);

        populatePolicies(base, topic, request);
        populateNews(base, topic, request);
        populateConsultations(base, topic, request);
        populatePublications(base, topic, request);
    }

    private void populatePolicies(HippoBean base, Topic topic, HstRequest request) {
        HstQuery query = topicLinkedBeansQuery(topic, base, Policy.class);
        query.addOrderByAscending("govscot:title");
        executeQueryLoggingException(query, request, "policies");
    }

    private void populateNews(HippoBean base, Topic topic, HstRequest request) {
        HstQuery query = topicLinkedBeansQuery(topic, base, News.class);
        query.addOrderByDescending("govscot:publishedDate");
        query.setLimit(3);
        executeQueryLoggingException(query, request, "news");
    }

    private void populateConsultations(HippoBean base, Topic topic, HstRequest request) {
        HstQuery query = topicLinkedBeansQuery(topic, base, Publication.class);
        query.addOrderByDescending("govscot:publicationDate");
        query.setLimit(3);

        try {
            Filter filter = query.createFilter();
            filter.addContains("govscot:publicationType", "consultation-paper");
            ((Filter) query.getFilter()).addAndFilter(filter);
        } catch (FilterException e) {
            LOG.error("Failed to filter results of consultation query", e);
        }

        executeQueryLoggingException(query, request, "consultations");
    }

    private void populatePublications(HippoBean base, Topic topic, HstRequest request) {
        HstQuery query = topicLinkedBeansQuery(topic, base, Publication.class);
        query.addOrderByDescending("govscot:publicationDate");
        query.setLimit(3);

        try {
            Filter filter = query.createFilter();
            filter.addNotContains("govscot:publicationType", "consultation-paper");
            ((Filter) query.getFilter()).addAndFilter(filter);
        } catch (FilterException e) {
            LOG.error("Failed to filter results of publication query", e);
        }

        executeQueryLoggingException(query, request, "publications");
    }

    private HstQuery topicLinkedBeansQuery(Topic topic, HippoBean base, Class linkedClass) {
        try {
            return ContentBeanUtils.createIncomingBeansQuery(
                    topic,
                    base,
                    "*/@hippo:docbase",
                    linkedClass,
                    false);
        } catch (QueryException e) {
            LOG.warn("Unable to get linked beans for topic {}", topic.getPath(), e);
            return null;
        }
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