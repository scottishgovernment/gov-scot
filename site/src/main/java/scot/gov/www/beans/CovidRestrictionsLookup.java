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

    @HippoEssentialsGenerated(internalName = "govscot:searchPageContent")
    public HippoHtml getSearchPageContent() {
        return getHippoHtml("govscot:searchPageContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:resultsContent")
    public HippoHtml getResultsContent() {
        return getHippoHtml("govscot:resultsContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:postcodeErrorMessage")
    public HippoHtml getPostcodeErrorMessage() {
        return getHippoHtml("govscot:postcodeErrorMessage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:serviceErrorMessage")
    public HippoHtml getServiceErrorMessage() {
        return getHippoHtml("govscot:serviceErrorMessage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:restrictionErrorMessage")
    public HippoHtml getRestrictionErrorMessage() {
        return getHippoHtml("govscot:restrictionErrorMessage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:englishPostcodeErrorMessage")
    public HippoHtml getEnglishPostcodeErrorMessage() {
        return getHippoHtml("govscot:englishPostcodeErrorMessage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:welshPostcodeErrorMessage")
    public HippoHtml getWelshPostcodeErrorMessage() {
        return getHippoHtml("govscot:welshPostcodeErrorMessage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:northernIrishPostcodeErrorMessage")
    public HippoHtml getNorthernIrishPostcodeErrorMessage() {
        return getHippoHtml("govscot:northernIrishPostcodeErrorMessage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:unrecognisedPostcodeErrorMessage")
    public HippoHtml getUnrecognisedPostcodeErrorMessage() {
        return getHippoHtml("govscot:unrecognisedPostcodeErrorMessage");
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
