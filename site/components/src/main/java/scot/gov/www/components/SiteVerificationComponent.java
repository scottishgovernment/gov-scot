package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import scot.gov.www.beans.Siteverification;

import java.util.List;

public class SiteVerificationComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response){
        HippoFolder folder = folder(request, "administration/");

        List<Siteverification> siteverifications = folder.getDocuments(Siteverification.class);
        request.setAttribute("siteverifications", siteverifications);
    }

    private HippoFolder folder(HstRequest request, String path) {
        HstRequestContext c = request.getRequestContext();
        HippoBean root = c.getSiteContentBaseBean();
        return root.getParentBean().getBean(path, HippoFolder.class);
    }

}
