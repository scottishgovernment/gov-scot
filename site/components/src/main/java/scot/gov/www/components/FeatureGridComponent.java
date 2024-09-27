package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoDocument;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = FeatureGridComponentInfo.class)
public class FeatureGridComponent extends CommonComponent {

    static final String TYPE = "govscot:featuregriditem";
    static final String CMS_PICKERS_DOCUMENTS_ONLY = "cms-pickers/documents-only";

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        FeatureGridComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute("document1", getHippoDocument(paramInfo.getImage1()));
        request.setAttribute("document2", getHippoDocument(paramInfo.getImage2()));
        request.setAttribute("document3", getHippoDocument(paramInfo.getImage3()));
        request.setAttribute("document4", getHippoDocument(paramInfo.getImage4()));
        request.setAttribute("weight", paramInfo.getWeight());

        request.setAttribute("fullwidth", paramInfo.getFullWidth());
        request.setAttribute("backgroundcolor", paramInfo.getBackgroundColor());
        request.setAttribute("foregroundcolor", paramInfo.getForegroundColor());
        request.setAttribute("neutrallinks", paramInfo.getNeutralLinks());
        request.setAttribute("showimages", paramInfo.getShowImages());
        request.setAttribute("smallvariant", paramInfo.getSmallVariant());
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());
    }

    HippoDocument getHippoDocument(String id) {
        return getHippoBeanForPath(id, HippoDocument.class);
    }
}
