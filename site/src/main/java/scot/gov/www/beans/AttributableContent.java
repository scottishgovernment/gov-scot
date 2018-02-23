package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.List;
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

    @HippoEssentialsGenerated(internalName = "govscot:responsibleRole")
    public HippoBean getResponsibleRole() {
        return getLinkedBean("govscot:responsibleRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:secondaryResponsibleRole")
    public List<HippoBean> getSecondaryResponsibleRole() {
        return getLinkedBeans("govscot:secondaryResponsibleRole",
                HippoBean.class);
    }
}
