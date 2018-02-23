package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;

@HippoEssentialsGenerated(internalName = "govscot:ContactInformation")
@Node(jcrType = "govscot:ContactInformation")
public class ContactInformation extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "govscot:email")
    public String getEmail() {
        return getProperty("govscot:email");
    }

    @HippoEssentialsGenerated(internalName = "govscot:twitter")
    public String getTwitter() {
        return getProperty("govscot:twitter");
    }

    @HippoEssentialsGenerated(internalName = "govscot:flickr")
    public String getFlickr() {
        return getProperty("govscot:flickr");
    }

    @HippoEssentialsGenerated(internalName = "govscot:website")
    public String getWebsite() {
        return getProperty("govscot:website");
    }

    @HippoEssentialsGenerated(internalName = "govscot:facebook")
    public String getFacebook() {
        return getProperty("govscot:facebook");
    }

    @HippoEssentialsGenerated(internalName = "govscot:youtube")
    public String getYoutube() {
        return getProperty("govscot:youtube");
    }

    @HippoEssentialsGenerated(internalName = "govscot:blog")
    public String getBlog() {
        return getProperty("govscot:blog");
    }
}
