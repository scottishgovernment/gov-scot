package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.Calendar;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import scot.gov.www.beans.ContactInformation;
import java.util.List;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import scot.gov.www.beans.BannerImages;

@HippoEssentialsGenerated(internalName = "govscot:Issue")
@Node(jcrType = "govscot:Issue")
public class Issue extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:featureDate")
    public Calendar getFeatureDate() {
        return getProperty("govscot:featureDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:featureDateTitle")
    public String getFeatureDateTitle() {
        return getProperty("govscot:featureDateTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:featuredItemsTitle")
    public String getFeaturedItemsTitle() {
        return getProperty("govscot:featuredItemsTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:contactTitle")
    public String getContactTitle() {
        return getProperty("govscot:contactTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:includeTwitterReplies")
    public Boolean getIncludeTwitterReplies() {
        return getProperty("govscot:includeTwitterReplies");
    }

    @HippoEssentialsGenerated(internalName = "govscot:showOnTopicsLandingPage")
    public Boolean getShowOnTopicsLandingPage() {
        return getProperty("govscot:showOnTopicsLandingPage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:issueTag")
    public String getIssueTag() {
        return getProperty("govscot:issueTag");
    }

    @HippoEssentialsGenerated(internalName = "govscot:featureDateSummary")
    public String getFeatureDateSummary() {
        return getProperty("govscot:featureDateSummary");
    }

    @HippoEssentialsGenerated(internalName = "govscot:socialMediaTitle")
    public String getSocialMediaTitle() {
        return getProperty("govscot:socialMediaTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:phone")
    public String getPhone() {
        return getProperty("govscot:phone");
    }

    @HippoEssentialsGenerated(internalName = "govscot:address")
    public String getAddress() {
        return getProperty("govscot:address");
    }

    @HippoEssentialsGenerated(internalName = "govscot:overview")
    public HippoHtml getOverview() {
        return getHippoHtml("govscot:overview");
    }

    @HippoEssentialsGenerated(internalName = "govscot:ContactInformation")
    public ContactInformation getContactInformation() {
        return getBean("govscot:ContactInformation", ContactInformation.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:featuredItems")
    public List<HippoBean> getFeaturedItems() {
        return getLinkedBeans("govscot:featuredItems", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:image")
    public BannerImages getImage() {
        return getLinkedBean("govscot:image", BannerImages.class);
    }
}
