package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.Calendar;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:Issue")
@Node(jcrType = "govscot:Issue")
public class Issue extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:featureDate")
    public Calendar getFeatureDate() {
        return getSingleProperty("govscot:featureDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:featureDateTitle")
    public String getFeatureDateTitle() {
        return getSingleProperty("govscot:featureDateTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:featuredItemsTitle")
    public String getFeaturedItemsTitle() {
        return getSingleProperty("govscot:featuredItemsTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:includeFeedback")
    public Boolean getIncludeFeedback() {
        return getSingleProperty("govscot:includeFeedback");
    }

    @HippoEssentialsGenerated(internalName = "govscot:showOnTopicsLandingPage")
    public Boolean getShowOnTopicsLandingPage() {
        return getSingleProperty("govscot:showOnTopicsLandingPage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:featureDateSummary")
    public String getFeatureDateSummary() {
        return getSingleProperty("govscot:featureDateSummary");
    }

    @HippoEssentialsGenerated(internalName = "govscot:overview")
    public HippoHtml getOverview() {
        return getHippoHtml("govscot:overview");
    }

    @HippoEssentialsGenerated(internalName = "govscot:featuredItems")
    public List<HippoBean> getFeaturedItems() {
        return getLinkedBeans("govscot:featuredItems", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:image")
    public BannerImages getImage() {
        return getLinkedBean("govscot:image", BannerImages.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:newsTags")
    public String[] getNewsTags() { return getMultipleProperty("govscot:newsTags"); }
}
