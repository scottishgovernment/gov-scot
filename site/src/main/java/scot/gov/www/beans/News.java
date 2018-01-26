package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import scot.gov.www.beans.ExternalLink;

@HippoEssentialsGenerated(internalName = "govscot:News")
@Node(jcrType = "govscot:News")
public class News extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:summary")
    public String getSummary() {
        return getProperty("govscot:summary");
    }

    @HippoEssentialsGenerated(internalName = "govscot:seoTitle")
    public String getSeoTitle() {
        return getProperty("govscot:seoTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:metaDescription")
    public String getMetaDescription() {
        return getProperty("govscot:metaDescription");
    }

    @HippoEssentialsGenerated(internalName = "hippostd:tags")
    public String[] getTags() {
        return getProperty("hippostd:tags");
    }

    @HippoEssentialsGenerated(internalName = "govscot:background")
    public HippoHtml getBackground() {
        return getHippoHtml("govscot:background");
    }

    @HippoEssentialsGenerated(internalName = "govscot:heroImage")
    public ExternalLink getHeroImage() {
        return getBean("govscot:heroImage", ExternalLink.class);
    }
}
