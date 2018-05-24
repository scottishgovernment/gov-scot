package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Policy;
import scot.gov.www.beans.PolicyInDetail;

import java.io.IOException;
import java.util.List;

/**
 * Get the information required to render a Policy or Policy in Detail page.
 */
public class PolicyComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PolicyComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        HippoBean document;

        try {
            document = request.getRequestContext().getContentBean();
            if(document == null) {
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
                response.setStatus(404);
                response.forward("/pagenotfound");
                return;
            }
        }  catch (IOException e) {
            throw new HstComponentException("forward failed", e);
        }

        List<PolicyInDetail> details = document.getParentBean().getChildBeans(PolicyInDetail.class);
        HippoBean prev = prevBean(policy, document, details);
        HippoBean next = nextBean(policy, document, details);
        request.setAttribute("document", document);
        request.setAttribute("index", policy);
        request.setAttribute("policyDetails", details);
        request.setAttribute("prev", prev);
        request.setAttribute("next", next);
    }

    private Policy getPolicy(HippoBean document) {
        HippoBean parent = document.getParentBean();
        List<Policy> policies = parent.getChildBeans(Policy.class);
        if (policies.isEmpty()) {
            LOG.info("No policy found under {}", document.getPath());
        }
        if (policies.size() > 1) {
            LOG.info("More than one policy found under {}, will use first", document.getPath());
        }
        return policies.get(0);
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