package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.Calendar;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:PageNotFound")
@Node(jcrType = "govscot:PageNotFound")
public class PageNotFound extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:archiveTitle")
    public String getArchiveTitle() {
        return getSingleProperty("govscot:archiveTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:archiveUrl")
    public String getArchiveUrl() {
        return getSingleProperty("govscot:archiveUrl");
    }

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

    @HippoEssentialsGenerated(internalName = "govscot:archiveContent")
    public HippoHtml getArchiveContent() {
        return getHippoHtml("govscot:archiveContent");
    }

    @HippoEssentialsGenerated(internalName = "govscot:archiveContentEpilogue")
    public HippoHtml getArchiveContentEpilogue() {
        return getHippoHtml("govscot:archiveContentEpilogue");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    public String getLabel() { return "news"; }
}
