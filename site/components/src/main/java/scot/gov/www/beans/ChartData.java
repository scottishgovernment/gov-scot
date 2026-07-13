package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "govscot:ChartData")
@Node(jcrType = "govscot:ChartData")
public class ChartData extends BaseDocument {

    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:data")
    public String getData() {
        return getSingleProperty("govscot:data");
    }
}
