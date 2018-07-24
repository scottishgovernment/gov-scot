package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:Group")
@Node(jcrType = "govscot:Group")
public class Group extends AttributableContent {
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

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    @HippoEssentialsGenerated(internalName = "govscot:relatedPolicies")
    public List<HippoBean> getRelatedPolicies() {
        return getLinkedBeans("govscot:relatedPolicies", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:members")
    public HippoHtml getMembers() {
        return getHippoHtml("govscot:members");
    }

    @HippoEssentialsGenerated(internalName = "govscot:contactDetails")
    public HippoHtml getContactDetails() {
        return getHippoHtml("govscot:contactDetails");
    }

    @HippoEssentialsGenerated(internalName = "govscot:relatedGroups")
    public List<HippoBean> getRelatedGroups() {
        return getLinkedBeans("govscot:relatedGroups", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:relatedPublications")
    public List<HippoBean> getRelatedPublications() {
        return getLinkedBeans("govscot:relatedPublications", HippoBean.class);
    }

    public String getLabel() { return ""; }
}
