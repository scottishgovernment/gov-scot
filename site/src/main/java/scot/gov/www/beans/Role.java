package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "govscot:Role")
@Node(jcrType = "govscot:Role")
public class Role extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:responsibilities")
    public HippoHtml getResponsibilities() {
        return getHippoHtml("govscot:responsibilities");
    }

    @HippoEssentialsGenerated(internalName = "govscot:incumbent")
    public HippoBean getIncumbent() {
        return getLinkedBean("govscot:incumbent", HippoBean.class);
    }
}
