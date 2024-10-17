package scot.gov.www.components;

import com.google.common.base.Strings;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocument;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = TextAndCardComponentInfo.class)
public class TextAndCardComponent extends CommonComponent {
    
    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        TextAndCardComponentInfo paramInfo = getComponentParametersInfo(request);
        setDocument("document1", paramInfo.getDocument(), request);
        request.setAttribute("document2", getHippoDocument(paramInfo.getImage()));
        request.setAttribute("neutrallinks", paramInfo.getNeutralLinks());
        request.setAttribute("showimages", paramInfo.getShowImages());
        request.setAttribute("smallvariant", paramInfo.getSmallVariant());
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());
    }

    void setDocument(String attr, String documentPath, HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        if (!Strings.isNullOrEmpty(documentPath)) {
            HippoBean root = context.getSiteContentBaseBean();
            request.setModel(attr, root.getBean(documentPath));
        }
    }

    HippoDocument getHippoDocument(String id) {
        return getHippoBeanForPath(id, HippoDocument.class);
    }
}
