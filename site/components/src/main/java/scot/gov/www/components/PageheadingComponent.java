package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.EssentialsDocumentComponent;

@ParametersInfo(type = PageheadingComponentInfo.class)
public class PageheadingComponent extends EssentialsDocumentComponent {

    @Override
    public void doBeforeRender(HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        PageheadingComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute("fullwidth", paramInfo.getFullWidth());
        request.setAttribute("backgroundcolor", paramInfo.getBackgroundColor());
        request.setAttribute("foregroundcolor", paramInfo.getForegroundColor());
        request.setAttribute("neutrallinks", paramInfo.getNeutralLinks());
        request.setAttribute("widetext", paramInfo.getWideText());
        request.setAttribute("verticalalign", paramInfo.getVerticalAlign());
        request.setAttribute("lightheader", paramInfo.getLightHeader());
        request.setAttribute("medianomargin", paramInfo.getMediaNoMargin());
        request.setAttribute("mediaaligndesktop", paramInfo.getMediaAlignDesktop());
        request.setAttribute("mediaalignmobile", paramInfo.getMediaAlignMobile());
    }
}
