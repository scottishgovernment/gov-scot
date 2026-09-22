package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.gov.www.beans.Indicator;
import scot.gov.www.beans.Outcome;

public class NPFIndicatorComponent extends CommonComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HstRequestContext context = request.getRequestContext();
        Indicator indicator = context.getContentBean(Indicator.class);
        if (indicator == null) {
            pageNotFound(response);
            return;
        }
        request.setAttribute("document", indicator);

        HippoBean outcomeFolder = indicator.getParentBean();
        Outcome outcome = outcomeFolder.getBean("index", Outcome.class);
        if (outcome != null) {
            request.setAttribute("parent", outcome);
        }
    }
}
