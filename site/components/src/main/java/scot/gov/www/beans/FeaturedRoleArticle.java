package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.List;
import scot.gov.www.beans.UpdateHistory;
import scot.gov.www.beans.RelatedArticle;

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
