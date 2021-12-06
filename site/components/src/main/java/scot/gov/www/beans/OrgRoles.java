package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:OrgRoles")
@Node(jcrType = "govscot:OrgRoles")
public class OrgRoles extends AttributableContent {
    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
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

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    @HippoEssentialsGenerated(internalName = "govscot:organisationName")
    public String getOrganisationName() {
        return getSingleProperty("govscot:organisationName");
    }

    @HippoEssentialsGenerated(internalName = "govscot:organisationDescription")
    public String getOrganisationDescription() {
        return getSingleProperty("govscot:organisationDescription");
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryOrganisationName")
    public String getSecondaryOrganisationName() {
        return getSingleProperty("govscot:secondaryOrganisationName");
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryOrganisationDescription")
    public String getSecondaryOrganisationDescription() {
        return getSingleProperty("govscot:secondaryOrganisationDescription");
    }

    @HippoEssentialsGenerated(internalName = "govscot:tertiaryOrganisationName")
    public String getTertiaryOrganisationName() {
        return getSingleProperty("govscot:tertiaryOrganisationName");
    }

    @HippoEssentialsGenerated(internalName = "govscot:tertiaryOrganisationDescription")
    public String getTertiaryOrganisationDescription() {
        return getSingleProperty("govscot:tertiaryOrganisationDescription");
    }

    public String getLabel() { return ""; }

    @HippoEssentialsGenerated(internalName = "govscot:orgRole")
    public List<HippoBean> getOrgRole() {
        return getLinkedBeans("govscot:orgRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryOrgRole")
    public List<HippoBean> getSecondaryOrgRole() {
        return getLinkedBeans("govscot:secondaryOrgRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:tertiaryOrgRole")
    public List<HippoBean> getTertiaryOrgRole() {
        return getLinkedBeans("govscot:tertiaryOrgRole", HippoBean.class);
    }

}
