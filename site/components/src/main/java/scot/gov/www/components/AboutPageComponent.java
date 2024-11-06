package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;

public class AboutPageComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HstRequestContext context = request.getRequestContext();
        HippoBean bean = context.getContentBean();

        // for the about section, if the path resolves as a folder then return a 404 page
        if (bean == null || bean.isHippoFolderBean()) {
            pageNotFound(response);
            return;
        }

        request.setModel(REQUEST_ATTR_DOCUMENT, bean);
    }
}
