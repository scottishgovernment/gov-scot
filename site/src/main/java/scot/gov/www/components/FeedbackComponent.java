package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;

import scot.gov.www.components.info.FeedbackComponentInfo;

@ParametersInfo(type = FeedbackComponentInfo.class)
public class FeedbackComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        final HstRequestContext requestContext = request.getRequestContext();
        ResolvedSiteMapItem resolvedSiteMapItem = requestContext.getResolvedSiteMapItem();
        HstComponentConfiguration hstComponentConfiguration = resolvedSiteMapItem.getHstComponentConfiguration();
        String layoutName = hstComponentConfiguration.getName();
        request.setAttribute("layoutName", layoutName);

        FeedbackComponentInfo info = getComponentParametersInfo(request);
        if (info.getFeedbackIsEnabled()) {
            request.setAttribute("feedbackIsEnabled", true);
        }
    }
}
