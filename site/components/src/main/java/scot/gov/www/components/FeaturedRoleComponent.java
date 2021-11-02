package scot.gov.www.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.News;
import scot.gov.www.beans.SpeechOrStatement;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

public class FeaturedRoleComponent extends EssentialsContentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FeaturedRoleComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        populateLatestRelatedItems(request, News.class, "news");
        populateLatestRelatedItems(request, SpeechOrStatement.class, "speeches");
    }

    private void populateLatestRelatedItems(HstRequest request, Class className, String attributeName) {
        HippoBean scope = request.getRequestContext().getSiteContentBaseBean();
        HstQuery query = HstQueryBuilder.create(scope)
                .ofTypes(className)
                .limit(3)
                .where(or(constraint("hippostd:tags").equalToCaseInsensitive("First Minister")))
                .orderByDescending("govscot:publicationDate").build();
        executeQueryLoggingException(query, request, attributeName);
    }

    static void executeQueryLoggingException(HstQuery query, HstRequest request, String name) {
        try {
            HstQueryResult result = query.execute();
            LOG.debug("executeQueryLoggingException {}, {}", name, result.getSize());

            request.setAttribute(name, result.getHippoBeans());
        } catch (QueryException e) {
            LOG.error("Failed to get {}", name, e);
            throw new HstComponentException(e);
        }
    }
}
