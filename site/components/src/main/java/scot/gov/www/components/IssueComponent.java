package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.*;

public class IssueComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(IssueComponent.class);

    private static final String DISPLAY_DATE = "govscot:displayDate";

    private static final String TITLE = "govscot:title";

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        super.doBeforeRender(request, response);

        HstRequestContext context = request.getRequestContext();
        HippoBean base = context.getSiteContentBaseBean();
        Issue issue = context.getContentBean(Issue.class);
        if (issue == null) {
            pageNotFound(response);
            return;
        }
        request.setAttribute("document", issue);

        populatePolicies(base, issue, request);
        populateNews(base, issue, request);
        populatePublications(base, issue, request);
    }

    static void populatePolicies(HippoBean base, HippoDocumentBean issue, HstRequest request) {
        populatePolicies(base, issue, request, -1);
    }

    static void populatePolicies(HippoBean base, HippoDocumentBean issue, HstRequest request, int limit) {
        try {
            HstQuery query = issueLinkedBeansQuery(issue, base, Policy.class, limit, TITLE);
            HstQueryResult result = query.execute();
            request.setAttribute("policies", result.getHippoBeans());
        } catch (QueryException e) {
            LOG.warn("Unable to get policies for issue {}", issue.getPath(), e);
        }
    }

    static void populateNews(HippoBean base, HippoDocumentBean issue, HstRequest request) {
        populateNews(base, issue, request, 4);
    }

    static void populateNews(HippoBean base, HippoDocumentBean issue, HstRequest request, int limit) {
        try {
            HstQuery taggedQuery = issueLinkedBeansQuery(issue, base, News.class, limit, "govscot:publicationDate");
            HippoBeanIterator taggedNews = taggedQuery.execute().getHippoBeans();
            request.setAttribute("news", taggedNews);
        } catch (QueryException e) {
            LOG.warn("Unable to get news for issue {}", issue.getPath(), e);
        }
    }


    static void populatePublications(HippoBean base, HippoDocumentBean issue, HstRequest request) {
        populatePublications(base, issue, request, 5);
    }

    static void populatePublications(HippoBean base, HippoDocumentBean issue, HstRequest request, int limit) {

        try {
            HstQuery publicationsQuery = issueLinkedBeansQuery(issue, base, Publication.class, limit, DISPLAY_DATE);
            HippoBeanIterator publications = publicationsQuery.execute().getHippoBeans();
            request.setAttribute("publications", publications);
        } catch (QueryException e) {
            LOG.error("Unable to get Publications for issue {}", issue.getPath(), e);
        }
    }

    static HstQuery issueLinkedBeansQuery(HippoDocumentBean issue, HippoBean base, Class linkedClass, int limit, String sortField)
            throws QueryException {

        HstQuery query = ContentBeanUtils.createIncomingBeansQuery(issue, base, "*/@hippo:docbase", linkedClass, true);
        query.addOrderByDescending(sortField);
        query.addOrderByAscending(TITLE);
        query.setLimit(limit);
        return query;
    }
}