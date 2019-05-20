package scot.gov.www.components;

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

import java.util.ArrayList;
import java.util.List;

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
        HstQuery query = HstQueryBuilder.create(scope).ofTypes(Policy.class).where(or(tagConstraints(news))).build();
        try {
            HstQueryResult result = query.execute();
            HippoBeanIterator it = result.getHippoBeans();
            List<String> policyNames = new ArrayList<>();
            while (it.hasNext()) {
                HippoBean policy = it.nextHippoBean();
                policyNames.add(policy.getParentBean().getName());
            }
            request.setAttribute("policies", policyNames);
        } catch (QueryException e) {
            throw new HstComponentException(e);
        }
    }

    private Constraint[] tagConstraints(News news) {
        ArrayList<Constraint> tagConstraints = new ArrayList<>();
        for (String tag : news.getPolicyTags()) {
            tagConstraints.add(constraint("govscot:policyTags").equalToCaseInsensitive(tag));
        }
        return tagConstraints.toArray(new Constraint[tagConstraints.size()]);
    }

}