package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:Home")
@Node(jcrType = "govscot:Home")
public class Home extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:howGovernmentWorks")
    public HippoHtml getHowGovernmentWorks() {
        return getHippoHtml("govscot:howGovernmentWorks");
    }

    @HippoEssentialsGenerated(internalName = "govscot:featuredItems")
    public List<HippoBean> getFeaturedItems() {
        return getLinkedBeans("govscot:featuredItems", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:flickrContent")
    public String getFlickrContent() {
        return getSingleProperty("govscot:flickrContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:twitterContent")
    public String getTwitterContent() {
        return getSingleProperty("govscot:twitterContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:youtubeContent")
    public String getYouTubeContent() {
        return getSingleProperty("govscot:youtubeContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:fmImagePortrait")
    public ColumnImage getFmImagePortrait() {
        return getLinkedBean("govscot:fmImagePortrait", ColumnImage.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:fmImageLandscape")
    public ColumnImage getFmImageLandscape() {
        return getLinkedBean("govscot:fmImageLandscape", ColumnImage.class);
    }
}
