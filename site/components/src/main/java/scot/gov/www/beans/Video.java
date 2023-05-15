package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "govscot:video")
@Node(jcrType = "govscot:video")
public class Video extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:alt")
    public String getAlt() {
        return getSingleProperty("govscot:alt");
    }

    @HippoEssentialsGenerated(internalName = "govscot:image")
    public HippoGalleryImageSet getImage() {
        return getLinkedBean("govscot:image", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:url")
    public String getUrl() {
        return getSingleProperty("govscot:url");
    }
}
