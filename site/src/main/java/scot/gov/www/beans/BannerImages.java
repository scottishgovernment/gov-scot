package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageBean;

@HippoEssentialsGenerated(internalName = "govscot:BannerImages")
@Node(jcrType = "govscot:BannerImages")
public class BannerImages extends HippoGalleryImageSet {
    @HippoEssentialsGenerated(internalName = "govscot:bannermobile")
    public HippoGalleryImageBean getBannermobile() {
        return getBean("govscot:bannermobile", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:bannermobiledoubled")
    public HippoGalleryImageBean getBannermobiledoubled() {
        return getBean("govscot:bannermobiledoubled",
                HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:bannertablet")
    public HippoGalleryImageBean getBannertablet() {
        return getBean("govscot:bannertablet", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:bannertabletdoubled")
    public HippoGalleryImageBean getBannertabletdoubled() {
        return getBean("govscot:bannertabletdoubled",
                HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:bannerdesktop")
    public HippoGalleryImageBean getBannerdesktop() {
        return getBean("govscot:bannerdesktop", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:bannerdesktopdoubled")
    public HippoGalleryImageBean getBannerdesktopdoubled() {
        return getBean("govscot:bannerdesktopdoubled",
                HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:bannerdesktophd")
    public HippoGalleryImageBean getBannerdesktophd() {
        return getBean("govscot:bannerdesktophd", HippoGalleryImageBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:bannerdesktophddoubled")
    public HippoGalleryImageBean getBannerdesktophddoubled() {
        return getBean("govscot:bannerdesktophddoubled",
                HippoGalleryImageBean.class);
    }
}
