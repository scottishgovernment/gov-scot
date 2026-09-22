package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "govscot:NPF")
@Node(jcrType = "govscot:NPF")
public class NPF extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:epilogue")
    public HippoHtml getEpilogue() {
        return getHippoHtml("govscot:epilogue");
    }

    @HippoEssentialsGenerated(internalName = "govscot:dashboardUrl")
    public String getDashboardUrl() {
        return getSingleProperty("govscot:dashboardUrl");
    }

}
