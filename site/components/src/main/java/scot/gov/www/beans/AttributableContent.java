package scot.gov.www.beans;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

import java.util.*;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.components.FilteredResultsComponent;

@HippoEssentialsGenerated(internalName = "govscot:AttributableContent")
@Node(jcrType = "govscot:AttributableContent")
public class AttributableContent extends SimpleContent {
    private static final Logger LOG = LoggerFactory.getLogger(AttributableContent.class);

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

    public HippoBean getPartOfBean() {
        try {
            HstRequestContext requestContext = RequestContextProvider.get();
            // find any Collection documents that link to the content bean in this request
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    this,
                    requestContext.getSiteContentBaseBean(),
                    "*/*/@hippo:docbase",
                    scot.gov.www.beans.Collection.class,
                    false);
            HstQueryResult result = query.execute();
            if (result.getHippoBeans().hasNext()) {
                return result.getHippoBeans().nextHippoBean();
            }
        } catch (QueryException e) {
            LOG.warn("Unable to get collections for content item {}", this.getPath(), e);
        }
        return null;
    }

}
