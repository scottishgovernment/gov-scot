package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "govscot:PublicationPage")
@Node(jcrType = "govscot:PublicationPage")
public class PublicationPage extends BaseDocument {
    private HippoBean parent;

    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    public String getLabel() { return "Publication Page"; }

    public void setParent(HippoBean parent){
        this.parent = parent;
    }

    public HippoBean getParent() {
        return this.parent;
    }
}
