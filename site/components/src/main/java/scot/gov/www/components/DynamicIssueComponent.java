package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = DynamicIssueInfo.class)
public class DynamicIssueComponent extends CommonComponent {

    private static final int LIMIT = 3;

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
        request.setAttribute("neutrallinks", paramInfo.getNeutralLinks());
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());

        if (paramInfo.getShowPolicies().booleanValue()) {
            IssueComponent.populatePolicies(baseBean, documentBean, request, LIMIT);
        }

        if (paramInfo.getShowNews().booleanValue()) {
            IssueComponent.populateNews(baseBean, documentBean, request, LIMIT);
        }

        if (paramInfo.getShowPublications().booleanValue()) {
            IssueComponent.populatePublications(baseBean, documentBean, request, LIMIT);
        }
    }

}
