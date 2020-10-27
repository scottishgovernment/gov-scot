package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.content.beans.Node;
import java.util.List;
import scot.gov.www.beans.UpdateHistory;

@HippoEssentialsGenerated(internalName = "govscot:CovidRestrictionsLookup")
@Node(jcrType = "govscot:CovidRestrictionsLookup")
public class CovidRestrictionsLookup extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:secondaryContent")
    public HippoHtml getSecondaryContent() {
        return getHippoHtml("govscot:secondaryContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:updateHistory")
    public List<UpdateHistory> getUpdateHistory() {
        return getChildBeansByName("govscot:updateHistory", UpdateHistory.class);
    }
}
