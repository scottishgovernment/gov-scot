package scot.gov.www.components;

import com.google.common.base.Strings;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = DynamicIssueInfo.class)
public class DynamicIssueComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        DynamicIssueInfo paramInfo = getComponentParametersInfo(request);
        setContentBeanForPath(paramInfo.getDocument(), request, response);

        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        HippoDocumentBean documentBean =   baseBean.getBean(paramInfo.getDocument());

        request.setAttribute("showPolicies", paramInfo.getShowPolicies());
        request.setAttribute("showNews", paramInfo.getShowNews());
        request.setAttribute("showPublications", paramInfo.getShowPublications());

        if (paramInfo.getShowPolicies().booleanValue()) {
            IssueComponent.populatePolicies(baseBean, documentBean, request);
        }

        if (paramInfo.getShowNews().booleanValue()) {
            IssueComponent.populateNews(baseBean, documentBean, request);
        }

        if (paramInfo.getShowPublications().booleanValue()) {
            IssueComponent.populatePublications(baseBean, documentBean, request);
        }
    }

}
