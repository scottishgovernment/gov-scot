package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;

public class PageNotFoundComponent extends CommonComponent {

    public void doBeforeRender(HstRequest request, HstResponse response) {
        HstRequestContext context = request.getRequestContext();
        HippoBean document = context.getContentBean();
        request.setAttribute("document", document);
        response.setStatus(404);
    }
}