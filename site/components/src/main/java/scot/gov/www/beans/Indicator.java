package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.Calendar;

@HippoEssentialsGenerated(internalName = "govscot:Indicator")
@Node(jcrType = "govscot:Indicator")
public class Indicator extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:status")
    public String getStatus() {
        return getSingleProperty("govscot:status");
    }

    @HippoEssentialsGenerated(internalName = "govscot:dateModified")
    public Calendar getDateModified() {
        return getSingleProperty("govscot:dateModified");
    }

    @HippoEssentialsGenerated(internalName = "govscot:dashboardUrl")
    public String getDashboardUrl() {
        return getSingleProperty("govscot:dashboardUrl");
    }
}
