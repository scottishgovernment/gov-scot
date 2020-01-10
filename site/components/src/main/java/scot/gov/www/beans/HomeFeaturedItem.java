package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "govscot:HomeFeaturedItem")
@Node(jcrType = "govscot:HomeFeaturedItem")
public class HomeFeaturedItem extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:overlayQuote")
    public String getOverlayQuote() {
        return getProperty("govscot:overlayQuote");
    }

    @HippoEssentialsGenerated(internalName = "govscot:overlayQuoteAttribution")
    public String getOverlayQuoteAttribution() {
        return getProperty("govscot:overlayQuoteAttribution");
    }

    @HippoEssentialsGenerated(internalName = "govscot:youtube")
    public String getYoutube() {
        return getProperty("govscot:youtube");
    }

    @HippoEssentialsGenerated(internalName = "govscot:teaserText")
    public HippoHtml getTeaserText() {
        return getHippoHtml("govscot:teaserText");
    }

    @HippoEssentialsGenerated(internalName = "govscot:link")
    public ExternalLink getLink() {
        return getBean("govscot:link", ExternalLink.class);
    }
    
    public FeaturedItems getImage() {
        return getLinkedBean("govscot:image", FeaturedItems.class);
    }
}
