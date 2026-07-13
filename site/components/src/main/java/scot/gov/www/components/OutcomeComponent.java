package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Indicator;
import scot.gov.www.beans.Outcome;

import java.util.List;

public class OutcomeComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(OutcomeComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HstRequestContext context = request.getRequestContext();
        Outcome outcome = context.getContentBean(Outcome.class);
        if (outcome == null) {
            pageNotFound(response);
            return;
        }
        request.setAttribute("document", outcome);

        List<Indicator> indicators = outcome.getParentBean().getChildBeans(Indicator.class);
        request.setAttribute("indicators", indicators);
    }
}
