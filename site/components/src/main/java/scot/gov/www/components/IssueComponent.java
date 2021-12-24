package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.*;

public class IssueComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(IssueComponent.class);

    private static final String DISPLAY_DATE = "govscot:displayDate";

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        HstRequestContext context = request.getRequestContext();
        HippoBean base = context.getSiteContentBaseBean();
        Issue issue = context.getContentBean(Issue.class);
        request.setAttribute("document", issue);

        populatePolicies(base, issue, request);
        populateNews(base, issue, request);
        populatePublications(base, issue, request);
    }

    private void populatePolicies(HippoBean base, Issue issue, HstRequest request) {
        try {
            HstQuery query = issueLinkedBeansQuery(issue, base, Policy.class, "govscot:title", -1);
            HstQueryResult result = query.execute();
            request.setAttribute("policies", result.getHippoBeans());
        } catch (QueryException e) {
            LOG.warn("Unable to get policies for issue {}", issue.getPath(), e);
        }
    }

    private void populateNews(HippoBean base, Issue issue, HstRequest request) {
        try {
            HstQuery taggedQuery = issueLinkedBeansQuery(issue, base, News.class, "govscot:publicationDate", 4);
            HippoBeanIterator taggedNews = taggedQuery.execute().getHippoBeans();
            request.setAttribute("news", taggedNews);
        } catch (QueryException e) {
            LOG.warn("Unable to get news for issue {}", issue.getPath(), e);
        }
    }

    private void populatePublications(HippoBean base, Issue issue, HstRequest request) {

        try {
            HstQuery publicationsQuery = issueLinkedBeansQuery(issue, base, Publication.class, DISPLAY_DATE, 5);
            LOG.info("publicationsQuery {}", publicationsQuery);
            HippoBeanIterator publications = publicationsQuery.execute().getHippoBeans();
            request.setAttribute("publications", publications);
        } catch (QueryException e) {
            LOG.error("Unable to get Publications for issue {}", issue.getPath(), e);
        }
    }

    private HstQuery issueLinkedBeansQuery(Issue issue, HippoBean base, Class linkedClass, String sortField, int limit)
            throws QueryException {

        HstQuery query = ContentBeanUtils.createIncomingBeansQuery(issue, base, "*/@hippo:docbase", linkedClass, true);
        query.addOrderByDescending(sortField);
        query.setLimit(limit);
        return query;
    }
}