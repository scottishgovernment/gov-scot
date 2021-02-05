package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageBean;

@HippoEssentialsGenerated(internalName = "govscot:CoverImage")
@Node(jcrType = "govscot:CoverImage")
public class CoverImage extends HippoGalleryImageSet {
    @HippoEssentialsGenerated(internalName = "govscot:smallcover")
    public HippoGalleryImageBean getSmallcover() {
        return getBean("govscot:smallcover", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:mediumcover")
    public HippoGalleryImageBean getMediumcover() {
        return getBean("govscot:mediumcover", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:largecover")
    public HippoGalleryImageBean getLargecover() {
        return getBean("govscot:largecover", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:xlargecover")
    public HippoGalleryImageBean getXlargecover() {
        return getBean("govscot:xlargecover", HippoGalleryImageBean.class);
    }
}
