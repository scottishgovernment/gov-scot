package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = TextComponentInfo.class)
public class TextComponent extends CommonComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        TextComponentInfo paramInfo = getComponentParametersInfo(request);
        setContentBeanForPath(paramInfo.getDocument(), request, response);
        request.setAttribute("position", paramInfo.getPosition());
        request.setAttribute("fullwidth", paramInfo.getFullWidth());
        request.setAttribute("backgroundcolor", paramInfo.getBackgroundColor());
        request.setAttribute("foregroundcolor", paramInfo.getForegroundColor());
        request.setAttribute("neutrallinks", paramInfo.getNeutralLinks());
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());
    }

}
