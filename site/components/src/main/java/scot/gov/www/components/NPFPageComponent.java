package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.gov.www.beans.NPF;
import scot.gov.www.beans.Outcome;

import java.util.List;

public class NPFPageComponent extends CommonComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HstRequestContext context = request.getRequestContext();
        NPF document = context.getContentBean(NPF.class);
        if (document == null) {
            pageNotFound(response);
            return;
        }
        request.setAttribute("document", document);

        HippoBean npfFolder = document.getParentBean();

        List<Outcome> outcomes = OutcomeGridUtils.getOutcomes(npfFolder);
        request.setAttribute("outcomes", outcomes);
        request.setAttribute("outcomeStatusCounts", OutcomeGridUtils.getOutcomeStatusCounts(npfFolder, outcomes));
    }
}
