package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Indicator;

public class IndicatorComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(IndicatorComponent.class);

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
    }
}
