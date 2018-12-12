package scot.gov.www.components;

import org.apache.commons.lang.time.StopWatch;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

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
        HstQuery taggedQuery = HstQueryBuilder.create(base)
                .ofTypes(News.class)
                .where(or(tagConstraints(issue)))
                .limit(4)
                .orderByDescending(PUBLICATIONDATE).build();
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

    private Constraint[] tagConstraints(Issue issue) {
        ArrayList<Constraint> tagConstraints = new ArrayList<>();
        tagConstraints.add(constraint("govscot:policyTags").equalTo(issue.getIssueTag()));
        return tagConstraints.toArray(new Constraint[tagConstraints.size()]);
    }

    private void populatePublications(HippoBean base, Issue issue, HstRequest request) {
        HstQuery publicationsQuery = issueLinkedBeansQuery(issue, base, Publication.class);
        HstQuery complexDocumentQuery = issueLinkedBeansQuery(issue, base, ComplexDocument.class);

        ArrayList<HippoBean> allLinkedPublications = new ArrayList<HippoBean>();

        try {
            HippoBeanIterator publications = publicationsQuery.execute().getHippoBeans();
            HippoBeanIterator complexDocuments = complexDocumentQuery.execute().getHippoBeans();

            while(publications.hasNext()) {
                HippoBean publication = publications.next();
                allLinkedPublications.add(publication);
            }

            while(complexDocuments.hasNext()) {
                HippoBean publication = complexDocuments.next();
                allLinkedPublications.add(publication);
            }

            allLinkedPublications.sort(Comparator.comparing(bean -> bean.getProperty(PUBLICATIONDATE)));
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