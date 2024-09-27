package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = HeaderComponentInfo.class)
public class HeaderComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);
        HeaderComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute("weight", paramInfo.getWeight());
        request.setAttribute("text", paramInfo.getText());
        request.setAttribute("position", paramInfo.getPosition());
        request.setAttribute("fullwidth", paramInfo.getFullWidth());
        request.setAttribute("backgroundcolor", paramInfo.getBackgroundColor());
        request.setAttribute("foregroundcolor", paramInfo.getForegroundColor());
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());
    }
}
