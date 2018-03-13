package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.www.beans.Policy;
import scot.gov.www.beans.PolicyInDetail;

import java.util.List;

/**
 * Get the information required to render a Policy or Policy in Detail page.
 */
public class PolicyComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        HippoBean document = request.getRequestContext().getContentBean();
        Policy policy = document.getParentBean().getBean("index", Policy.class);
        List<PolicyInDetail> details = document.getParentBean().getChildBeans(PolicyInDetail.class);
        HippoBean prev = prevBean(policy, document, details);
        HippoBean next = nextBean(policy, document, details);
        request.setAttribute("document", document);
        request.setAttribute("policy", policy);
        request.setAttribute("policyDetails", details);
        request.setAttribute("prev", prev);
        request.setAttribute("next", next);
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