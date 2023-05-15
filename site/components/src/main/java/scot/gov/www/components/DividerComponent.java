package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = DividerComponentInfo.class)
public class DividerComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        DividerComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute("fullwidth", paramInfo.getFullWidth());
        request.setAttribute("backgroundcolor", paramInfo.getBackgroundColor());
        request.setAttribute("foregroundcolor", paramInfo.getForegroundColor());
    }
}
