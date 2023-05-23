package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "govscot:featuregriditem")
@Node(jcrType = "govscot:featuregriditem")
public class Featuregriditem extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:alt")
    public String getAlt() {
        return getSingleProperty("govscot:alt");
    }

    @HippoEssentialsGenerated(internalName = "govscot:image")
    public HippoGalleryImageSet getImage() {
        return getLinkedBean("govscot:image", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:link")
    public HippoBean getLink() {
        return getLinkedBean("govscot:link", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:externalLink")
    public String getExternalLink() {
        return getSingleProperty("govscot:externalLink");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }
}
