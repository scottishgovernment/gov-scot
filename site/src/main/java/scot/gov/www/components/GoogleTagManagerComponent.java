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

        String GTMname = hstComponentConfiguration.getName();
        String GTMid = resolvedSiteMapItem.getPathInfo();

        request.setAttribute("GTMname", GTMname);
        request.setAttribute("GTMid", GTMid);
    }
}
