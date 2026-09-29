package scot.gov.www.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import scot.gov.www.beans.Indicator;
import scot.gov.www.beans.Outcome;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared logic for rendering the grid of national outcomes (and their indicator status
 * tallies) used by both the national outcomes landing page and the standalone Outcomes
 * catalog component, so the two stay in sync.
 */
public class OutcomeGridUtils {

    private OutcomeGridUtils() {
    }

    public static List<Outcome> getOutcomes(HippoBean outcomesFolder) {
        try {
            HstQuery query = HstQueryBuilder.create(outcomesFolder)
                    .ofTypes(Outcome.class)
                    .build();
            HstQueryResult result = query.execute();
            List<Outcome> outcomes = new ArrayList<>();
            result.getHippoBeans().forEachRemaining(b -> outcomes.add((Outcome) b));
            return outcomes;
        } catch (QueryException e) {
            throw new HstComponentException("Failed to query national outcomes", e);
        }
    }

    // seed a zeroed status count map for every outcome, keyed by the outcome folder name,
    // then make a single query for all indicators in the outcomes tree and tally each one against
    // its own outcome - this keeps the whole summary to two queries regardless of how many
    // outcomes/indicators there are, rather than querying indicators once per outcome.
    public static Map<String, Map<String, Long>> getOutcomeStatusCounts(HippoBean outcomesFolder, List<Outcome> outcomes) {
        Map<String, Map<String, Long>> outcomeStatusCounts = new LinkedHashMap<>();
        for (Outcome outcome : outcomes) {
            Map<String, Long> counts = new LinkedHashMap<>();
            counts.put("improving", 0L);
            counts.put("stable", 0L);
            counts.put("worsening", 0L);
            outcomeStatusCounts.put(outcome.getParentBean().getName(), counts);
        }

        try {
            HstQuery indicatorQuery = HstQueryBuilder.create(outcomesFolder)
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
            throw new HstComponentException("Failed to query national outcome indicator statuses", e);
        }
        return outcomeStatusCounts;
    }
}
