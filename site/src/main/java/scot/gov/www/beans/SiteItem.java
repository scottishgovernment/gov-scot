package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "govscot:SiteItem")
@Node(jcrType = "govscot:SiteItem")
public class SiteItem extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:ExternalLink")
    public ExternalLink getExternalLink() {
        return getBean("govscot:ExternalLink", ExternalLink.class);
    }
}
