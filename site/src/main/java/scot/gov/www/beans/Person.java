package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.List;


@HippoEssentialsGenerated(internalName = "govscot:Person")
@Node(jcrType = "govscot:Person")
public class Person extends AttributableContent {
    private List<HippoBean> roles;

    public List<HippoBean> getRoles() {
        return roles;
    }

    public void setRoles(List<HippoBean> roles) {
        this.roles = roles;
    }

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

    @HippoEssentialsGenerated(internalName = "govscot:ContactInformation")
    public ContactInformation getContactInformation() {
        return getBean("govscot:ContactInformation", ContactInformation.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    @HippoEssentialsGenerated(internalName = "govscot:roleTitle")
    public String getRoleTitle() {
        return getProperty("govscot:roleTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:postalAddress")
    public HippoHtml getPostalAddress() {
        return getHippoHtml("govscot:postalAddress");
    }

    public String getLabel() {
        return "role";
    }

    public People getImage() {
        return getLinkedBean("govscot:image", People.class);
    }

    public String getName() { return getTitle(); }
}
