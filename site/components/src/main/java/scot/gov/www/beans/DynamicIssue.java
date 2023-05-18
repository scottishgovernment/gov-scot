package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "govscot:DynamicIssue")
@Node(jcrType = "govscot:DynamicIssue")
public class DynamicIssue extends SimpleContent {

    @HippoEssentialsGenerated(internalName = "govscot:showOnTopicsLandingPage")
    public Boolean getShowOnTopicsLandingPage() {
        return getSingleProperty("govscot:showOnTopicsLandingPage");
    }

}
