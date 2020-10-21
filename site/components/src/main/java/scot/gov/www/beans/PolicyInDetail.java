package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "govscot:PolicyInDetail")
@Node(jcrType = "govscot:PolicyInDetail")
public class PolicyInDetail extends SimpleContent {

    public String getLabel() { return "policy"; }

    @HippoEssentialsGenerated(internalName = "govscot:policyTags")
    public String[] getPolicyTags() {
        return getMultipleProperty("govscot:policyTags");
    }
}
