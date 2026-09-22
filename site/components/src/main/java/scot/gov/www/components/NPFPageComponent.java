package scot.gov.www.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.gov.www.beans.NPF;
import scot.gov.www.beans.Indicator;
import scot.gov.www.beans.Outcome;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        List<Outcome> outcomes;
        try {
            HstQuery query = HstQueryBuilder.create(npfFolder)
                    .ofTypes(Outcome.class)
                    .build();
            HstQueryResult result = query.execute();
            outcomes = new ArrayList<>();
            result.getHippoBeans().forEachRemaining(b -> outcomes.add((Outcome) b));
            request.setAttribute("outcomes", outcomes);
        } catch (QueryException e) {
            throw new HstComponentException("Failed to query NPF outcomes", e);
        }

        // seed a zeroed status count map for every outcome, keyed by the outcome folder name,
        // then make a single query for all indicators in the NPF tree and tally each one against
        // its own outcome - this keeps the whole summary to two queries regardless of how many
        // outcomes/indicators there are, rather than querying indicators once per outcome.
        Map<String, Map<String, Long>> outcomeStatusCounts = new LinkedHashMap<>();
        for (Outcome outcome : outcomes) {
            Map<String, Long> counts = new LinkedHashMap<>();
            counts.put("improving", 0L);
            counts.put("stable", 0L);
            counts.put("worsening", 0L);
            outcomeStatusCounts.put(outcome.getParentBean().getName(), counts);
        }

        try {
            HstQuery indicatorQuery = HstQueryBuilder.create(npfFolder)
                    .ofTypes(Indicator.class)
                    .build();
            HstQueryResult indicatorResult = indicatorQuery.execute();
            indicatorResult.getHippoBeans().forEachRemaining(b -> {
                Indicator indicator = (Indicator) b;
                String status = indicator.getStatus();
                if (status == null) {
                    return;
                }
                Map<String, Long> counts = outcomeStatusCounts.get(indicator.getParentBean().getName());
                if (counts != null && counts.containsKey(status)) {
                    counts.merge(status, 1L, Long::sum);
                }
            });
        } catch (QueryException e) {
            throw new HstComponentException("Failed to query NPF indicator statuses", e);
        }
        request.setAttribute("outcomeStatusCounts", outcomeStatusCounts);
    }
}
