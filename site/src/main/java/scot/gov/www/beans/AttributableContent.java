package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

import java.util.*;

import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "govscot:AttributableContent")
@Node(jcrType = "govscot:AttributableContent")
public class AttributableContent extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:topics")
    public List<HippoBean> getTopics() {
        return getLinkedBeans("govscot:topics", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:responsibleDirectorate")
    public HippoBean getResponsibleDirectorate() {
        return getLinkedBean("govscot:responsibleDirectorate", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryResponsibleDirectorate")
    public List<HippoBean> getSecondaryResponsibleDirectorate() {
        return getLinkedBeans("govscot:secondaryResponsibleDirectorate",
                HippoBean.class);
    }

    public List<HippoBean> getAllDirectorates() {
        List<HippoBean> directorates = new ArrayList<>();
        HippoBean responsibleDirectorate = this.getResponsibleDirectorate();
        if (responsibleDirectorate != null){
            directorates.add(this.getResponsibleDirectorate());
        }
        directorates.addAll(this.getSecondaryResponsibleDirectorate());
        return directorates;
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

    public List<HippoBean> getAllResponsibleRoles() {
        List<HippoBean> roles = new ArrayList<>();
        HippoBean responsibleRole = this.getResponsibleRole();
        if (responsibleRole != null){
            roles.add(this.getResponsibleRole());
        }
        roles.addAll(this.getSecondaryResponsibleRole());
        return roles;
    }

    @HippoEssentialsGenerated(internalName = "govscot:orgRole")
    public List<HippoBean> getOrgRole() {
        return getLinkedBeans("govscot:orgRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryOrgRole")
    public List<HippoBean> getSecondaryOrgRole() {
        return getLinkedBeans("govscot:secondaryOrgRole", HippoBean.class);
    }
}
