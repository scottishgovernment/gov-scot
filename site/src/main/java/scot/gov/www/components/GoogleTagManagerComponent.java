package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;

public class GoogleTagManagerComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        final HstRequestContext requestContext = request.getRequestContext();

        ResolvedSiteMapItem resolvedSiteMapItem = requestContext.getResolvedSiteMapItem();

        HstComponentConfiguration hstComponentConfiguration = resolvedSiteMapItem.getHstComponentConfiguration();

        String gtmName = hstComponentConfiguration.getName();
        String gtmId = resolvedSiteMapItem.getPathInfo();

        request.setAttribute("gtmName", gtmName);
        request.setAttribute("gtmId", gtmId);

        String hostGroupName = requestContext.getResolvedMount().getResolvedVirtualHost().getVirtualHost().getHostGroupName();

        if ("www".equals(hostGroupName) || "beta".equals(hostGroupName)) {
            request.setAttribute("useLiveAnalytics", true);
        }
    }
}
