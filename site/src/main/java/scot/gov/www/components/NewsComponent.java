package scot.gov.www.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.gov.www.beans.News;
import scot.gov.www.beans.Policy;

import java.util.*;

import static java.util.stream.Collectors.*;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

/**
 * Component backing News pages. Queries to find policies that this news item is attributed to.
 *
 * Note that this will need to be updated when MGS-5175 is completed.
 */
public class NewsComponent extends EssentialsContentComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        // find any policies that share a news tag with this news item.
        News news = (News) request.getRequestContext().getContentBean();
        HippoBean scope = request.getRequestContext().getSiteContentBaseBean();
        request.setAttribute("policies", policyNames(scope, news));
    }

    /**
     * Get the policy names for this news item.
     *
     * This is done by querying all Policy pages that share a value of govscot:policyTags
     */
    private List<String> policyNames(HippoBean scope, News news) {
        Set<String> tags = nonBlankPolicyTags(news);
        if (tags.isEmpty()) {
            return Collections.emptyList();
        }

        HstQuery query = HstQueryBuilder.create(scope).ofTypes(Policy.class).where(or(tagConstraints(tags))).build();

        try {
            HstQueryResult result = query.execute();
            HippoBeanIterator it = result.getHippoBeans();
            List<String> policyNames = new ArrayList<>();
            while (it.hasNext()) {
                HippoBean policy = it.nextHippoBean();
                HippoBean folder = policy.getParentBean();
                policyNames.add(folder.getName());
            }
            return policyNames;
        } catch (QueryException e) {
            throw new HstComponentException(e);
        }
    }

    /**
     * Get the set of all non-blank policy tags for this news item
     */
    private Set<String> nonBlankPolicyTags(News news) {
        if (news.getPolicyTags() == null) {
            return Collections.emptySet();
        }

        return Arrays.stream(news.getPolicyTags()).filter(StringUtils::isNotBlank).collect(toSet());
    }

    /**
     * Convert the set of tags into an array of constraints.
     */
    private Constraint[] tagConstraints(Set<String> tags) {
        return tags.stream()
                .map(tag -> constraint("govscot:newsTags").equalTo(tag))
                .collect(toList())
                .toArray(new Constraint[tags.size()]);
    }

}