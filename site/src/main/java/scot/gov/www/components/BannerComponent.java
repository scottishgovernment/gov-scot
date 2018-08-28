package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;


public class BannerComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        final HstRequestContext requestContext = request.getRequestContext();

        String hostGroupName = requestContext.getResolvedMount().getResolvedVirtualHost().getVirtualHost().getHostGroupName();

        if (!"www".equals(hostGroupName)) {
            request.setAttribute("showBetaBanner", true);
        }

        request.setAttribute("footrue", true);
        request.setAttribute("barfalse", false);
    }
}
