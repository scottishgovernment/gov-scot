package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.www.beans.SimpleContent;

/**
 * Created by z441571 on 09/04/2018.
 */
public class FilteredResultsContentComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        super.doBeforeRender(request, response);

        HippoBean bean = request.getRequestContext().getContentBean();
        SimpleContent index = bean.getBean("index", SimpleContent.class);
        request.setAttribute("index", index);

    }
}
