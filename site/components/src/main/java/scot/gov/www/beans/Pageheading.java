package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "govscot:pageheading")
@Node(jcrType = "govscot:pageheading")
public class Pageheading extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:alt")
    public String getAlt() {
        return getSingleProperty("govscot:alt");
    }

    @HippoEssentialsGenerated(internalName = "govscot:description")
    public HippoHtml getDescription() {
        return getHippoHtml("govscot:description");
    }

    @HippoEssentialsGenerated(internalName = "govscot:image")
    public HippoGalleryImageSet getImage() {
        return getLinkedBean("govscot:image", HippoGalleryImageSet.class);
    }
}
