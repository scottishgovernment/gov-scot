package scot.gov.www.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Role;

import java.util.Collections;

public class PersonComponent extends EssentialsContentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PersonComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HippoDocumentBean bean = (HippoDocumentBean) request.getRequestContext().getContentBean();
        HippoBean siteBean = request.getRequestContext().getSiteContentBaseBean();
        Class<Role> mappingClass = Role.class;
        try {
            HstQuery q = ContentBeanUtils.createIncomingBeansQuery(bean, siteBean, "govscot:incumbent/@hippo:docbase", mappingClass, true);
            HstQueryResult result = q.execute();
            request.setAttribute("roles", result.getHippoBeans());
        } catch (QueryException e) {
            LOG.error("Query error getting supporting roles", e);
            request.setAttribute("roles", Collections.emptyList());
        }
    }
}
