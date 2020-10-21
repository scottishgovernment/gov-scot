package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.standard.HippoGalleryImageBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;

@HippoEssentialsGenerated(internalName = "govscot:FeaturedItems")
@Node(jcrType = "govscot:FeaturedItems")
public class FeaturedItems extends HippoGalleryImageSet {

    @HippoEssentialsGenerated(internalName = "govscot:featuredlarge")
    public HippoGalleryImageBean getFeaturedlarge() {
        return getBean("govscot:featuredlarge", HippoGalleryImageBean.class);
    }
}
