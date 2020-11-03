package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.content.beans.Node;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:CovidRestrictionsLookup")
@Node(jcrType = "govscot:CovidRestrictionsLookup")
public class CovidRestrictionsLookup extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:secondaryContent")
    public HippoHtml getSecondaryContent() {
        return getHippoHtml("govscot:secondaryContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryContent")
    public HippoHtml getSearchPageContent() {
        return getHippoHtml("govscot:searchPageContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:resutsContent")
    public HippoHtml getResultsContent() {
        return getHippoHtml("govscot:resultsContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:errorMessage")
    public String getErrorMessage() {
        return getProperty("govscot:errorMessage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:hintMessage")
    public String getHintMessage() {
        return getProperty("govscot:hintMessage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:updateHistory")
    public List<UpdateHistory> getUpdateHistory() {
        return getChildBeansByName("govscot:updateHistory", UpdateHistory.class);
    }
}
