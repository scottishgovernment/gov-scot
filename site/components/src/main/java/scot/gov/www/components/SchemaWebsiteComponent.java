package scot.gov.www.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.gov.www.channel.WebsiteInfo;

public class SchemaWebsiteComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        Mount mount = request.getRequestContext().getResolvedMount().getMount();
        WebsiteInfo websiteInfo = mount.getChannelInfo();
        request.setAttribute("sitetitle", websiteInfo.getSiteTitle());
        request.setAttribute("isSearchEnabled", websiteInfo.isSearchEnabled());
        request.setAttribute("baseBean", request.getRequestContext().getSiteContentBaseBean());
    }

}