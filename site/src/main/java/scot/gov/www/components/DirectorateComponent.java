package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Directorate;
import scot.gov.www.beans.Policy;

public class DirectorateComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DirectorateComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        HstRequestContext context = request.getRequestContext();
        Directorate document = context.getContentBean(Directorate.class);
        request.setAttribute("document", document);

        // find all policies that link to this Directorate
        try {
            HippoBean baseBean = context.getSiteContentBaseBean();
            HstQuery policyQuery = ContentBeanUtils.createIncomingBeansQuery(
                    document, baseBean, "govscot:*/@hippo:docbase", Policy.class, false);
            policyQuery.addOrderByDescending("govscot:title");
            HstQueryResult policies = policyQuery.execute();
            request.setAttribute("policies", policies);
        } catch (QueryException e) {
            LOG.warn("Unable to get Policies for directorate {}", document.getPath(), e);
        }
    }

}