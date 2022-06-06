package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.CommonComponent;

public class FooterComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        HippoFolderBean footer = baseBean.getBean("siteitems");
        request.setAttribute("children", footer.getChildBeans(HippoBean.class));
    }

}
