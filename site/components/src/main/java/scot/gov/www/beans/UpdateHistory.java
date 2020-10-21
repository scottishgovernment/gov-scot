package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import java.util.Calendar;

@HippoEssentialsGenerated(internalName = "govscot:UpdateHistory")
@Node(jcrType = "govscot:UpdateHistory")
public class UpdateHistory extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "govscot:lastUpdated")
    public Calendar getLastUpdated() {
        return getSingleProperty("govscot:lastUpdated");
    }

    @HippoEssentialsGenerated(internalName = "govscot:updateText")
    public String getUpdateText() {
        return getSingleProperty("govscot:updateText");
    }
}
