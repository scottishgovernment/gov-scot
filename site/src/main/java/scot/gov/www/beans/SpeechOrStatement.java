package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "govscot:SpeechOrStatement")
@Node(jcrType = "govscot:SpeechOrStatement")
public class SpeechOrStatement extends Publication {
    @HippoEssentialsGenerated(internalName = "govscot:location")
    public String getLocation() {
        return getProperty("govscot:location");
    }

    @HippoEssentialsGenerated(internalName = "govscot:speechDeliveredBy")
    public String getSpeechDeliveredBy() {
        return getProperty("govscot:speechDeliveredBy");
    }
}
