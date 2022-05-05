package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:Directorate")
@Node(jcrType = "govscot:Directorate")
public class Directorate extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:ContactInformation")
    public ContactInformation getContactInformation() {
        return getBean("govscot:ContactInformation", ContactInformation.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:orgRole")
    public List<HippoBean> getOrgRole() {
        return getLinkedBeans("govscot:orgRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:relatedNews")
    public List<HippoBean> getRelatedNews() {
        return getLinkedBeans("govscot:relatedNews", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryOrgRole")
    public List<HippoBean> getSecondaryOrgRole() {
        return getLinkedBeans("govscot:secondaryOrgRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:relatedPublications")
    public List<HippoBean> getRelatedPublications() {
        return getLinkedBeans("govscot:relatedPublications", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:responsibleRole")
    public HippoBean getResponsibleRole() {
        return getLinkedBean("govscot:responsibleRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryResponsibleRole")
    public List<HippoBean> getSecondaryResponsibleRole() {
        return getLinkedBeans("govscot:secondaryResponsibleRole",
                HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:postalAddress")
    public HippoHtml getPostalAddress() {
        return getHippoHtml("govscot:postalAddress");
    }

    public String getLabel() { return "directorate"; }
}
