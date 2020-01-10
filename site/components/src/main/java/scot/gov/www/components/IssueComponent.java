package scot.gov.www.components;

import org.apache.commons.lang.time.StopWatch;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.*;

import java.util.*;

public class IssueComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(IssueComponent.class);

    private static final String PUBLICATIONDATE = "govscot:publicationDate";

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
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    issue,
                    base,
                    "*/@hippo:docbase",
                    Policy.class,
                    false);
            query.addOrderByAscending("govscot:title");
            executeQueryLoggingException(query, request, "policies");
        } catch (QueryException e) {
            LOG.warn("Unable to get policies for issue {}", issue.getPath(), e);
        }
    }

    private void populateNews(HippoBean base, Issue issue, HstRequest request) {
        // Search for news with this issue's prgloo tag
        HstQuery taggedQuery = issueLinkedBeansQuery(issue, base, News.class);
        taggedQuery.setLimit(4);
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            HippoBeanIterator taggedNews = taggedQuery.execute().getHippoBeans();

            request.setAttribute("news", taggedNews);
            stopWatch.stop();
            LOG.debug("Issue page found {} tagged news items, took: {}", taggedNews.getSize(), stopWatch.getTime());

        } catch (QueryException e) {
            throw new HstComponentException(e);
        }
    }

    private void populatePublications(HippoBean base, Issue issue, HstRequest request) {
        HstQuery publicationsQuery = issueLinkedBeansQuery(issue, base, Publication.class);
        ArrayList<HippoBean> allLinkedPublications = new ArrayList<HippoBean>();

        try {
            HippoBeanIterator publications = publicationsQuery.execute().getHippoBeans();

            while(publications.hasNext()) {
                HippoBean publication = publications.next();
                allLinkedPublications.add(publication);
            }

            allLinkedPublications.sort(Comparator.comparing(this::dateToCompare));
            Collections.reverse(allLinkedPublications);
            List<HippoBean> latestPublications = allLinkedPublications;

            if (allLinkedPublications.size() > 5){
                latestPublications = allLinkedPublications.subList(0, 5);
            }

            request.setAttribute("publications", latestPublications);

        } catch (QueryException e) {
            LOG.error("Unable to get Publications for issue {}", issue.getPath(), e);
        }
    }

    Calendar dateToCompare(HippoBean bean) {
        Calendar publicationDate = bean.getProperty(PUBLICATIONDATE);
        if (publicationDate != null) {
            return publicationDate;
        }

        // this bean has no publication date, default to the hippostdpubwf:lastModificationDate
        return bean.getProperty("hippostdpubwf:lastModificationDate");
    }

    private HstQuery issueLinkedBeansQuery(Issue issue, HippoBean base, Class linkedClass) {
        try {
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    issue,
                    base,
                    "*/@hippo:docbase",
                    linkedClass,
                    true);
            query.addOrderByDescending(PUBLICATIONDATE);
            return query;
        } catch (QueryException e) {
            LOG.warn("Unable to get linked beans for issue {}", issue.getPath(), e);
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