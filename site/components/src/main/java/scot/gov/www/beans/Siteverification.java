package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "govscot:siteverification")
@Node(jcrType = "govscot:siteverification")
public class Siteverification extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "govscot:code")
    public String getCode() {
        return getSingleProperty("govscot:code");
    }

    @HippoEssentialsGenerated(internalName = "govscot:type")
    public String getType() {
        return getSingleProperty("govscot:type");
    }
}
