package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.Calendar;

@HippoEssentialsGenerated(internalName = "govscot:FOI")
@Node(jcrType = "govscot:FOI")
public class FOI extends Publication {
    @HippoEssentialsGenerated(internalName = "govscot:foiNumber")
    public String getFoiNumber() {
        return getProperty("govscot:foiNumber");
    }

    @HippoEssentialsGenerated(internalName = "govscot:dateReceived")
    public Calendar getDateReceived() {
        return getProperty("govscot:dateReceived");
    }

    @HippoEssentialsGenerated(internalName = "govscot:dateResponded")
    public Calendar getDateResponded() {
        return getProperty("govscot:dateResponded");
    }

    @HippoEssentialsGenerated(internalName = "govscot:sme")
    public String getSme() {
        return getProperty("govscot:sme");
    }

    @HippoEssentialsGenerated(internalName = "govscot:request")
    public HippoHtml getRequest() {
        return getHippoHtml("govscot:request");
    }

    @HippoEssentialsGenerated(internalName = "govscot:response")
    public HippoHtml getResponse() {
        return getHippoHtml("govscot:response");
    }
}
