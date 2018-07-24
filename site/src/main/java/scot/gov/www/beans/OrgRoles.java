package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "govscot:OrgRoles")
@Node(jcrType = "govscot:OrgRoles")
public class OrgRoles extends AttributableContent {
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

    @HippoEssentialsGenerated(internalName = "govscot:organisationName")
    public String getOrganisationName() {
        return getProperty("govscot:organisationName");
    }

    @HippoEssentialsGenerated(internalName = "govscot:organisationDescription")
    public String getOrganisationDescription() {
        return getProperty("govscot:organisationDescription");
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryOrganisationName")
    public String getSecondaryOrganisationName() {
        return getProperty("govscot:secondaryOrganisationName");
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryOrganisationDescription")
    public String getSecondaryOrganisationDescription() {
        return getProperty("govscot:secondaryOrganisationDescription");
    }

    public String getLabel() { return ""; }
}
