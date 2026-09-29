package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.gov.www.beans.Outcome;

import java.util.List;

/**
 * Catalog component that can be dropped onto any page to show the same grid of
 * national outcome cards (with indicator status tallies) as the national outcomes
 * landing page.
 */
@ParametersInfo(type = OutcomesComponentInfo.class)
public class OutcomesComponent extends CommonComponent {

    static final String OUTCOMES_FOLDER = "national-outcomes";

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        OutcomesComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute("backgroundcolor", paramInfo.getBackgroundColor());

        HstRequestContext context = request.getRequestContext();
        HippoBean root = context.getSiteContentBaseBean();
        HippoBean outcomesFolder = root.getBean(OUTCOMES_FOLDER);
        if (outcomesFolder == null) {
            return;
        }

        List<Outcome> outcomes = OutcomeGridUtils.getOutcomes(outcomesFolder);
        request.setAttribute("outcomes", outcomes);
        request.setAttribute("outcomeStatusCounts", OutcomeGridUtils.getOutcomeStatusCounts(outcomesFolder, outcomes));
    }
}
