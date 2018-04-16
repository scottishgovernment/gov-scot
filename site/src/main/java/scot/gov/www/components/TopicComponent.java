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
        try {
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    topic,
                    base,
                    "*/@hippo:docbase",
                    Policy.class,
                    false);
            query.addOrderByAscending("govscot:title");
            executeQueryLoggingException(query, request, "policies");
        } catch (QueryException e) {
            LOG.warn("Unable to get policies for topic {}", topic.getPath(), e);
        }
    }

    private void populateNews(HippoBean base, Topic topic, HstRequest request) {
        try {
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    topic,
                    base,
                    "*/@hippo:docbase",
                    News.class,
                    false);
            query.addOrderByDescending("govscot:publishedDate");
            query.setLimit(3);
            executeQueryLoggingException(query, request, "news");
        } catch (QueryException e) {
            LOG.warn("Unable to get news for topic {}", topic.getPath(), e);
        }
    }

    private void populateConsultations(HippoBean base, Topic topic, HstRequest request) {
        try {
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    topic,
                    base,
                    "*/@hippo:docbase",
                    Publication.class,
                    false);
            query.addOrderByDescending("govscot:publicationDate");
            query.setLimit(3);
            addFilter(query, filter -> filter.addContains("govscot:publicationType", "consultation"));
            executeQueryLoggingException(query, request, "consultations");

        } catch (QueryException e) {
            LOG.warn("Unable to get consultations for topic {}", topic.getPath(), e);
        }
    }

    private void populatePublications(HippoBean base, Topic topic, HstRequest request) {
        try {
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    topic,
                    base,
                    "*/@hippo:docbase",
                    Publication.class,
                    false);
            query.addOrderByDescending("govscot:publicationDate");
            query.setLimit(3);
            addFilter(query, filter -> filter.addNotContains("govscot:publicationType", "consultation"));
            executeQueryLoggingException(query, request, "publications");
        } catch (QueryException e) {
            LOG.warn("Unable to get publications for topic {}", topic.getPath(), e);
        }
    }

    private void addFilter(HstQuery query, FilterAdder adder) throws FilterException {
        if (adder == null) {
            return;
        }

        Filter filter = query.createFilter();
        query.setFilter(filter);
        adder.add(filter);
    }

    @FunctionalInterface
    public interface FilterAdder {
        void add(Filter filter) throws FilterException;
    }

    private void executeQueryLoggingException(HstQuery query, HstRequest request, String name) {
        try {
            HstQueryResult result = query.execute();
            LOG.info("Result count is {}", result.getTotalSize());
            request.setAttribute(name, result.getHippoBeans());
        } catch (QueryException e) {
            LOG.error("Failed to get {}", name, e);
        }
    }


}