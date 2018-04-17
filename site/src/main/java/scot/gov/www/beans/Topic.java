package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.List;
import org.hippoecm.hst.content.beans.standard.HippoBean;

import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:Topic")
@Node(jcrType = "govscot:Topic")
public class Topic extends SimpleContent {

    @HippoEssentialsGenerated(internalName = "govscot:responsibleRole")
    public List<HippoBean> getResponsibleRoles() {
        return getLinkedBeans("govscot:responsibleRole", HippoBean.class);
    }
}
