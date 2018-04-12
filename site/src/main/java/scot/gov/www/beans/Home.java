package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "govscot:Home")
@Node(jcrType = "govscot:Home")
public class Home extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:howGovernmentWorks")
    public HippoHtml getHowGovernmentWorks() {
        return getHippoHtml("govscot:howGovernmentWorks");
    }
}
