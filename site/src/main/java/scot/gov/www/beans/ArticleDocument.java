package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "govscot:articledocument")
@Node(jcrType = "govscot:articledocument")
public class ArticleDocument extends ContentDocument {
    @HippoEssentialsGenerated(internalName = "govscot:additional")
    public HippoHtml getAdditional() {
        return getHippoHtml("govscot:additional");
    }
}
