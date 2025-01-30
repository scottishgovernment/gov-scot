package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "govscot:ConsultationResponseType")
@Node(jcrType = "govscot:ConsultationResponseType")
public class ConsultationResponseType extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "govscot:type")
    public String getType() {
        return getSingleProperty("govscot:type");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }
}
