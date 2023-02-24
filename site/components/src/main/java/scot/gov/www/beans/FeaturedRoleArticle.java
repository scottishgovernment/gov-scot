package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:FeaturedRoleArticle")
@Node(jcrType = "govscot:FeaturedRoleArticle")
public class FeaturedRoleArticle extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:updateHistory")
    public List<UpdateHistory> getUpdateHistory() {
        return getChildBeansByName("govscot:updateHistory", UpdateHistory.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:relatedarticle")
    public RelatedArticle getRelatedarticle() {
        return getBean("govscot:relatedarticle", RelatedArticle.class);
    }
}
