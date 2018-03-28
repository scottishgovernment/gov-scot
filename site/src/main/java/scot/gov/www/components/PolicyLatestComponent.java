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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.News;
import scot.gov.www.beans.Policy;
import scot.gov.www.beans.PolicyInDetail;

import java.util.ArrayList;
import java.util.List;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

public class PolicyLatestComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PolicyLatestComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        HippoBean document = request.getRequestContext().getContentBean();
        LOG.info("document {}", document.getCanonicalPath());
        Policy policy = document.getParentBean().getBean("index", Policy.class);
        List<PolicyInDetail> details = document.getParentBean().getChildBeans(PolicyInDetail.class);
        LOG.info("paremt {}", document.getParentBean().getCanonicalPath());
        LOG.info("policy {}", policy.getCanonicalPath());
        HippoBean prev = prevBean(policy, document, details);
        HippoBean next = nextBean(policy, document, details);
        request.setAttribute("document", document);
        request.setAttribute("index", policy);
        request.setAttribute("prev", prev);
        request.setAttribute("next", next);


        HippoBeanIterator newsIt = getLatestNews(request, policy);
        List<HippoBean> all = new ArrayList<>();
        newsIt.forEachRemaining(all::add);
        all.addAll(policy.getRelatedItems());
        LOG.info("{} related items, total size is {}", policy.getRelatedItems().size(), all.size());
        request.setAttribute("latest", all);
    }

    private HippoBeanIterator getLatestNews(HstRequest request, Policy policy) {

        HippoBean scope = request.getRequestContext().getSiteContentBaseBean();
        HstQuery query = HstQueryBuilder.create(scope)
                .ofTypes(News.class)
                .where(or(tagConstraints(policy)))
                .orderByDescending("govscot:publishedDate").build();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            HstQueryResult result = query.execute();
            stopWatch.stop();
            LOG.info("result count: {}, took: {}", result.getTotalSize(), stopWatch.getTime());
            return result.getHippoBeans();
        } catch (QueryException e) {
            throw new HstComponentException(e);
        }
    }

    private Constraint [] tagConstraints(Policy policy) {
        ArrayList<Constraint> tagConstraints = new ArrayList<>();
        for (String tag : policy.getPolicyTags()) {
            LOG.info("policy tag: {}", tag);
            tagConstraints.add(constraint("govscot:policyTags").equalTo(tag));
        }
        return tagConstraints.toArray(new Constraint[tagConstraints.size()]);
    }

    private HippoBean prevBean(HippoBean policy, HippoBean document, List<PolicyInDetail> details) {
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

}
