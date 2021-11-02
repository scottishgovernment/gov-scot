package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "govscot:NavigationCard")
@Node(jcrType = "govscot:NavigationCard")
public class NavigationCard extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public String getContent() {
        return getProperty("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:externallink")
    public String getExternallink() {
        return getProperty("govscot:externallink");
    }

    @HippoEssentialsGenerated(internalName = "govscot:image")
    public ColumnImage getImage() {
        return getLinkedBean("govscot:image", ColumnImage.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:internallink")
    public HippoBean getInternallink() {
        return getLinkedBean("govscot:internallink", HippoBean.class);
    }
}
