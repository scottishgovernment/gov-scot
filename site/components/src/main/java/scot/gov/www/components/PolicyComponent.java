package scot.gov.www.components;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.time.StopWatch;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.News;
import scot.gov.www.beans.Policy;
import scot.gov.www.beans.PolicyInDetail;
import scot.gov.www.beans.PolicyLatest;

import java.io.IOException;
import java.util.*;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

/**
 * Get the information required to render a Policy or Policy in Detail page.
 */
public class PolicyComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PolicyComponent.class);

    private static final String LATEST = "latest";

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        HippoBean document;

        try {
            document = request.getRequestContext().getContentBean();
            if(document == null) {
                LOG.info("404 for {}", request.getRequestURL());
                response.setStatus(404);
                response.forward("/pagenotfound");
                return;
            }
        }  catch (IOException e) {
            throw new HstComponentException("forward failed", e);
        }

        Policy policy;
        try {
            policy = getPolicy(document);
            if(policy == null) {
                LOG.info("404 for {}", request.getRequestURL());
                response.setStatus(404);
                response.forward("/pagenotfound");
                return;
            }
        }  catch (IOException e) {
            throw new HstComponentException("forward failed", e);
        }

        // combine policy in detail reporting tags with parent reporting tags
        setReportingTags(document, policy, request);

        List<PolicyInDetail> details = document.getParentBean().getChildBeans(PolicyInDetail.class);
        HippoBean prev = prevBean(policy, document, details);
        HippoBean next = nextBean(policy, document, details);
        request.setAttribute("policies", Collections.singletonList(policy.getParentBean().getName()));
        request.setAttribute("document", document);
        request.setAttribute("index", policy);
        request.setAttribute("policyDetails", details);
        request.setAttribute("prev", prev);
        request.setAttribute("next", next);

        // if this is the latest page then also include latest info
        if (request.getPathInfo().endsWith("/latest/")) {
            List<HippoBean> all = getLatestNews(request, policy);
            PolicyLatest latest = (PolicyLatest) document;
            all.addAll(latest.getRelatedItems());
            Collections.sort(all, this::compareDate);
            Collections.reverse(all);
            request.setAttribute(LATEST, all);
        }
    }

    private int compareDate(HippoBean left, HippoBean right) {
        return ObjectUtils.compare(dateToCompare(left), dateToCompare(right));
    }

    Calendar dateToCompare(HippoBean bean) {
        Calendar publicationDate = bean.getSingleProperty("govscot:publicationDate");
        if (publicationDate != null) {
            return publicationDate;
        }

        // this bean has no publication date, default to the hippostdpubwf:publicationDate
        return bean.getSingleProperty("hippostdpubwf:publicationDate");
    }

    private List<HippoBean> getLatestNews(HstRequest request, Policy policy) {
        if (policy.getNewsTags() == null || policy.getNewsTags().length == 0) {
            return new ArrayList<>();
        }

        HippoBean scope = request.getRequestContext().getSiteContentBaseBean();
        HstQuery query = HstQueryBuilder.create(scope)
                .ofTypes(News.class)
                .where(or(tagConstraints(policy)))
                .orderByDescending("govscot:publicationDate").build();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            HstQueryResult result = query.execute();
            stopWatch.stop();

            List<HippoBean> all = new ArrayList<>();
            result.getHippoBeans().forEachRemaining(all::add);
            return all;
        } catch (QueryException e) {
            throw new HstComponentException(e);
        }
    }

    private Constraint[] tagConstraints(Policy policy) {
        ArrayList<Constraint> tagConstraints = new ArrayList<>();
        for (String tag : policy.getNewsTags()) {
            tagConstraints.add(constraint("govscot:policyTags").equalToCaseInsensitive(tag));
        }
        return tagConstraints.toArray(new Constraint[tagConstraints.size()]);
    }

    private Policy getPolicy(HippoBean document) {
        HippoBean parent = document.getParentBean();
        List<Policy> policies = parent.getChildBeans(Policy.class);
        if (policies.isEmpty()) {
            LOG.info("No policy found under {}", document.getPath());
            return null;
        }
        if (policies.size() > 1) {
            LOG.info("More than one policy found under {}, will use first", document.getPath());
        }

        return policies.get(0);
    }

    private HippoBean prevBean(HippoBean policy, HippoBean document, List<PolicyInDetail> details) {

        if (LATEST.equals(document.getName())) {
            return policy;
        }

        // if the document being rendered is the policy, then there is no previous bean
        if (document.isSelf(policy)) {
            return null;
        }

        int index = details.indexOf(document);
        if (index == 0) {
            // for the first details page the prev is the policy
            return policy;
        }

        return details.get(index - 1);
    }

    private HippoBean nextBean(HippoBean policy, HippoBean document, List<PolicyInDetail> details) {

        if (LATEST.equals(document.getName())) {
            return details.isEmpty() ? null : details.get(0);
        }

        // if the document being rendered is the policy, return the first details page (if there is one)
        if (document.isSelf(policy)) {
            return nextBeanForPolicy(details);
        }

        // if this is the last details page then next is null
        int index = details.indexOf(document);
        if (index == details.size() - 1) {
            return null;
        }

        return details.get(index + 1);
    }

    private HippoBean nextBeanForPolicy(List<PolicyInDetail> details) {
        if (details.isEmpty()) {
            return null;
        }
        return details.get(0);
    }

    private void setReportingTags(HippoBean document, HippoBean policy, HstRequest request){
        // if the current document is the main policy document, no action needed
        if (document.getClass() == Policy.class){
            return;
        }

        Set<String> reportingTags = new HashSet<>();
        addTags(reportingTags, document);
        addTags(reportingTags, policy);
        request.setAttribute("reportingTags", reportingTags);
    }

    private void addTags(Set<String> reportingTags, HippoBean document) {
        String [] tags = document.getMultipleProperty("govscot:reportingTags");
        if (tags != null) {
            reportingTags.addAll(Arrays.asList(tags));
        }
    }

}
