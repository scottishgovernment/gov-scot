package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.List;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "govscot:Policy")
@Node(jcrType = "govscot:Policy")
public class Policy extends AttributableContent {
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

    @HippoEssentialsGenerated(internalName = "govscot:actions")
    public String getActions() {
        return getProperty("govscot:actions");
    }

    @HippoEssentialsGenerated(internalName = "govscot:background")
    public String getBackground() {
        return getProperty("govscot:background");
    }

    @HippoEssentialsGenerated(internalName = "govscot:billsAndLegislation")
    public String getBillsAndLegislation() {
        return getProperty("govscot:billsAndLegislation");
    }

    @HippoEssentialsGenerated(internalName = "govscot:contact")
    public String getContact() {
        return getProperty("govscot:contact");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    @HippoEssentialsGenerated(internalName = "govscot:background")
    public String getBackground() {
        return getProperty("govscot:background");
    }

    @HippoEssentialsGenerated(internalName = "govscot:billsAndLegislation")
    public String getBillsAndLegislation() {
        return getProperty("govscot:billsAndLegislation");
    }

    @HippoEssentialsGenerated(internalName = "govscot:contact")
    public String getContact() {
        return getProperty("govscot:contact");
    }

    @HippoEssentialsGenerated(internalName = "govscot:actions")
    public String getActions() {
        return getProperty("govscot:actions");
    }

    @HippoEssentialsGenerated(internalName = "govscot:policyTags")
    public String[] getPolicyTags() {
        return getProperty("govscot:policyTags");
    }

    @HippoEssentialsGenerated(internalName = "govscot:relatedItems")
    public List<HippoBean> getRelatedItems() {
        return getLinkedBeans("govscot:relatedItems", HippoBean.class);
    }
}
