package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "govscot:FeaturedRoleBiography")
@Node(jcrType = "govscot:FeaturedRoleBiography")
public class FeaturedRoleBiography extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:person")
    public HippoBean getPerson() {
        return getLinkedBean("govscot:person", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:image")
    public ColumnImage getImage() {
        return getLinkedBean("govscot:image", ColumnImage.class);
    }
}
