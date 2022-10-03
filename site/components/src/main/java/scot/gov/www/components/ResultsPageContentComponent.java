package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.www.beans.SimpleContent;

public class ResultsPageContentComponent extends BaseHstComponent {

    private static final String INDEX = "index";

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        super.doBeforeRender(request, response);

        HippoBean bean = request.getRequestContext().getContentBean();
        SimpleContent index = bean.getBean(INDEX, SimpleContent.class);

        if (index != null){
            request.setAttribute(INDEX, index);
        } else {
            request.setAttribute(INDEX, bean);
        }
    }

}
