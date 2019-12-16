package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;

import scot.gov.www.beans.Issue;
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

        HippoBean contentBean = request.getRequestContext().getContentBean();
        boolean feedbackIsEnabled;
        if (isContentBean(contentBean)) {
            // if this is an issue then display of feedback is controlled by the user
            Issue issue = request.getRequestContext().getContentBean(Issue.class);
            feedbackIsEnabled = issue.getIncludeFeedback().booleanValue();
        } else {
            //
            FeedbackComponentInfo info = getComponentParametersInfo(request);
            feedbackIsEnabled =  info.getFeedbackIsEnabled();
        }

        request.setAttribute("feedbackIsEnabled", feedbackIsEnabled);
    }

    boolean isContentBean(HippoBean bean) {
        return bean != null && bean instanceof Issue;
    }
}
