package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoDocument;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = ThreeImageCardsComponentInfo.class)
public class ThreeImageCardsComponent extends CommonComponent {

    static final String TYPE = "govscot:navigationcardcontentblock";

    static final String CMS_PICKERS_DOCUMENTS_ONLY = "cms-pickers/documents-only";

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        ThreeImageCardsComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute("document1", getHippoDocument(paramInfo.getImage1()));
        request.setAttribute("document2", getHippoDocument(paramInfo.getImage2()));
        request.setAttribute("document3", getHippoDocument(paramInfo.getImage3()));

        request.setAttribute("fullwidth", paramInfo.getFullWidth());
        request.setAttribute("showimages", paramInfo.getShowImages());
        request.setAttribute("smallvariant", paramInfo.getSmallVariant());
        request.setAttribute("neutrallinks", paramInfo.getNeutralLinks());
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());
        request.setAttribute("greycards", paramInfo.getGreyCards());
    }

    HippoDocument getHippoDocument(String id) {
        return getHippoBeanForPath(id, HippoDocument.class);
    }
}
