package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.EssentialsDocumentComponent;

@ParametersInfo(type = VideoComponentInfo.class)
public class VideoComponent extends EssentialsDocumentComponent {

    // nothing required - the document will be available in the request,
    // and the other options from the params info as 'cparam', for example 'cparam.theme'.
    @Override
    public void doBeforeRender(HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);
        VideoComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute("fullwidth", paramInfo.getFullWidth());
        request.setAttribute("backgroundcolor", paramInfo.getBackgroundColor());
        request.setAttribute("foregroundcolor", paramInfo.getForegroundColor());
    }
}
