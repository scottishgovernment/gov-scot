package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

import java.util.*;

import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "govscot:AttributableContent")
@Node(jcrType = "govscot:AttributableContent")
public class AttributableContent extends SimpleContent {
    private ArrayList<HippoBean> collections = new ArrayList<>();

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

    /**
     * Get all of the directorates (primary and secondary)
     *
     * The primary directorate should be first followed by the secondary directorates with duplicates removed.
     */
    public List<HippoBean> getAllDirectorates() {
        Set<String> taken = new HashSet<>();
        List<HippoBean> directorates = new ArrayList<>();
        addIfNotTaken(directorates, this.getResponsibleDirectorate(), taken);
        this.getSecondaryResponsibleDirectorate()
                .stream()
                .forEach(directorate -> addIfNotTaken(directorates, directorate, taken));
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

    /**
     * Get all of the responsible roles (primary and secondary)
     *
     * The primary role should be first followed by the secondary roles with duplicates removed.
     */

    public List<HippoBean> getAllResponsibleRoles() {
        Set<String> taken = new HashSet<>();
        List<HippoBean> roles = new ArrayList<>();
        addIfNotTaken(roles, this.getResponsibleRole(), taken);
        this.getSecondaryResponsibleRole()
                .stream()
                .forEach(role -> addIfNotTaken(roles, role, taken));
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

    private void addIfNotTaken(List<HippoBean> beans, HippoBean bean, Set<String> taken) {
        if (bean == null) {
            return;
        }

        if (taken.contains(bean.getIdentifier())) {
            return;
        }

        beans.add(bean);
        taken.add(bean.getIdentifier());
    }

    public void setCollections(List<HippoBean> collections){
        this.collections.clear();
        this.collections.addAll(collections);
    }

    public List<HippoBean> getCollections() {
        return this.collections;
    }
}
