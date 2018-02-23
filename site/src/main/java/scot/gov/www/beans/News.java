package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import scot.gov.www.beans.ExternalLink;
import java.util.Calendar;
import java.util.List;

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

    @HippoEssentialsGenerated(internalName = "govscot:publishedDate")
    public Calendar getPublishedDate() {
        return getProperty("govscot:publishedDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:externalId")
    public String getExternalId() {
        return getProperty("govscot:externalId");
    }

    @HippoEssentialsGenerated(internalName = "govscot:updatedDate")
    public Calendar getUpdatedDate() {
        return getProperty("govscot:updatedDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:attachments")
    public List<ExternalLink> getAttachments() {
        return getChildBeansByName("govscot:attachments", ExternalLink.class);
    }
}
