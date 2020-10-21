package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.MourningBanner;

public class MourningBannerComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(MourningBannerComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        HstRequestContext context = request.getRequestContext();
        HippoBean scope = context.getSiteContentBaseBean();

        // check for a Mourning Banner item
        HstQuery query = HstQueryBuilder.create(scope).ofTypes(MourningBanner.class).limit(1).build();
        try {
            HstQueryResult result = query.execute();
            if (result.getSize() > 0) {
                request.setAttribute("mourningBanner", result.getHippoBeans().nextHippoBean());
            }
        } catch (QueryException e) {
            LOG.error("Failed to get {}", "mourning banner", e);
        }
    }

}