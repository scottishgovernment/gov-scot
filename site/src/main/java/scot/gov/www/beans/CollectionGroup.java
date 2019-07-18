package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.List;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "govscot:CollectionGroup")
@Node(jcrType = "govscot:CollectionGroup")
public class CollectionGroup extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "govscot:groupTitle")
    public String getGroupTitle() {
        return getProperty("govscot:groupTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:order")
    public Boolean getOrder() {
        return getProperty("govscot:order");
    }

    @HippoEssentialsGenerated(internalName = "govscot:description")
    public HippoHtml getDescription() {
        return getHippoHtml("govscot:description");
    }

    @HippoEssentialsGenerated(internalName = "govscot:collectionItems")
    public List<HippoBean> getCollectionItems() {
        return getLinkedBeans("govscot:collectionItems", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:highlight")
    public Boolean getHighlight() {
        return getProperty("govscot:highlight");
    }

}
