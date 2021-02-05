package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageBean;

@HippoEssentialsGenerated(internalName = "govscot:People")
@Node(jcrType = "govscot:People")
public class People extends HippoGalleryImageSet {
    @HippoEssentialsGenerated(internalName = "govscot:small")
    public HippoGalleryImageBean getSmall() {
        return getBean("govscot:small", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:smalldoubled")
    public HippoGalleryImageBean getSmalldoubled() {
        return getBean("govscot:smalldoubled", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:medium")
    public HippoGalleryImageBean getMedium() {
        return getBean("govscot:medium", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:mediumdoubled")
    public HippoGalleryImageBean getMediumdoubled() {
        return getBean("govscot:mediumdoubled", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:large")
    public HippoGalleryImageBean getLarge() {
        return getBean("govscot:large", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:largedoubled")
    public HippoGalleryImageBean getLargedoubled() {
        return getBean("govscot:largedoubled", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:xlarge")
    public HippoGalleryImageBean getXlarge() {
        return getBean("govscot:xlarge", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:xlargedoubled")
    public HippoGalleryImageBean getXlargedoubled() {
        return getBean("govscot:xlargedoubled", HippoGalleryImageBean.class);
    }
}
