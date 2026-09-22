package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.gov.www.beans.NPF;
import scot.gov.www.beans.Indicator;
import scot.gov.www.beans.Outcome;

import java.util.List;

public class NPFOutcomeComponent extends CommonComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HstRequestContext context = request.getRequestContext();
        Outcome outcome = context.getContentBean(Outcome.class);
        if (outcome == null) {
            pageNotFound(response);
            return;
        }
        request.setAttribute("document", outcome);

        List<Indicator> indicators = outcome.getParentBean().getChildBeans(Indicator.class);
        request.setAttribute("indicators", indicators);

        HippoBean npfFolder = outcome.getParentBean().getParentBean();
        NPF npfPage = npfFolder.getBean("index", NPF.class);
        if (npfPage != null) {
            request.setAttribute("parent", npfPage);
        }
    }
}
