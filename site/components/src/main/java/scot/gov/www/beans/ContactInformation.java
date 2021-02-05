package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;

@HippoEssentialsGenerated(internalName = "govscot:ContactInformation")
@Node(jcrType = "govscot:ContactInformation")
public class ContactInformation extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "govscot:email")
    public String getEmail() {
        return getSingleProperty("govscot:email");
    }

    @HippoEssentialsGenerated(internalName = "govscot:twitter")
    public String getTwitter() {
        return getSingleProperty("govscot:twitter");
    }

    @HippoEssentialsGenerated(internalName = "govscot:flickr")
    public String getFlickr() {
        return getSingleProperty("govscot:flickr");
    }

    @HippoEssentialsGenerated(internalName = "govscot:website")
    public String getWebsite() {
        return getSingleProperty("govscot:website");
    }

    @HippoEssentialsGenerated(internalName = "govscot:facebook")
    public String getFacebook() {
        return getSingleProperty("govscot:facebook");
    }

    @HippoEssentialsGenerated(internalName = "govscot:youtube")
    public String getYoutube() {
        return getSingleProperty("govscot:youtube");
    }

    @HippoEssentialsGenerated(internalName = "govscot:blog")
    public String getBlog() {
        return ("govscot:blog");
    }
}
