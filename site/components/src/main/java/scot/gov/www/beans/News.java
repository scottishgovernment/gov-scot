package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.onehippo.forge.feed.api.FeedType;
import org.onehippo.forge.feed.api.annot.SyndicationElement;
import org.onehippo.forge.feed.api.transform.CalendarToDateConverter;
import org.onehippo.forge.feed.api.transform.PathLinkResolver;
import org.onehippo.forge.feed.api.transform.rss.StringToDescriptionConverter;

import java.util.Calendar;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:News")
@Node(jcrType = "govscot:News")
public class News extends SimpleContent {

    @HippoEssentialsGenerated(internalName = "govscot:title")
    @SyndicationElement(type = FeedType.RSS, name = "title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
    }

    @SyndicationElement(type = FeedType.RSS, name = "description", converter = StringToDescriptionConverter.class)
    @HippoEssentialsGenerated(internalName = "govscot:summary")
    public String getSummary() {
        return getSingleProperty("govscot:summary");
    }

    @HippoEssentialsGenerated(internalName = "govscot:seoTitle")
    public String getSeoTitle() {
        return getSingleProperty("govscot:seoTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:metaDescription")
    public String getMetaDescription() {
        return getSingleProperty("govscot:metaDescription");
    }

    @HippoEssentialsGenerated(internalName = "hippostd:tags")
    public String[] getTags() {
        return getMultipleProperty("hippostd:tags");
    }

    @HippoEssentialsGenerated(internalName = "govscot:background")
    public HippoHtml getBackground() {
        return getHippoHtml("govscot:background");
    }

    @HippoEssentialsGenerated(internalName = "govscot:heroImage")
    public ExternalLink getHeroImage() {
        return getBean("govscot:heroImage", ExternalLink.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:publicationDate")
    @SyndicationElement(type = FeedType.RSS, name = "pubDate", converter = CalendarToDateConverter.class)
    public Calendar getPublicationDate() {
        return getSingleProperty("govscot:publicationDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:externalId")
    public String getExternalId() {
        return getSingleProperty("govscot:externalId");
    }

    @HippoEssentialsGenerated(internalName = "govscot:updatedDate")
    public Calendar getUpdatedDate() {
        return getSingleProperty("govscot:updatedDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:attachments")
    public List<ExternalLink> getAttachments() {
        return getChildBeansByName("govscot:attachments", ExternalLink.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:topics")
    public List<HippoBean> getTopics() {
        return getLinkedBeans("govscot:topics", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:policyTags")
    public String[] getPolicyTags() {
        return getMultipleProperty("govscot:policyTags");
    }

    @HippoEssentialsGenerated(internalName = "govscot:orgRole")
    public List<HippoBean> getOrgRole() {
        return getLinkedBeans("govscot:orgRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryOrgRole")
    public List<HippoBean> getSecondaryOrgRole() {
        return getLinkedBeans("govscot:secondaryOrgRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    public String getLabel() {
        return "news";
    }

    @SyndicationElement(type = FeedType.RSS, name = "link", transformer = PathLinkResolver.class)
    public String getURL() {
        return "news/".concat(getSingleProperty("govscot:prglooslug"));
    }

    @HippoEssentialsGenerated(internalName = "govscot:reportingTags")
    public String[] getReportingTags() {
        return getMultipleProperty("govscot:reportingTags");
    }
}
