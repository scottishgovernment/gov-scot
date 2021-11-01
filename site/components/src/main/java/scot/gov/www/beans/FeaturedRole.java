package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:FeaturedRole")
@Node(jcrType = "govscot:FeaturedRole")
public class FeaturedRole extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:responsibilities")
    public HippoHtml getResponsibilities() {
        return getHippoHtml("govscot:responsibilities");
    }

    @HippoEssentialsGenerated(internalName = "govscot:incumbent")
    public Person getIncumbent() {
        return getLinkedBean("govscot:incumbent", Person.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:image")
    public ColumnImage getImage() {
        return getLinkedBean("govscot:image", ColumnImage.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:feature")
    public NavigationCard getFeature() {
        return getBean("govscot:feature", NavigationCard.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:featurelist")
    public List<NavigationCard> getFeaturelist() {
        return getChildBeansByName("govscot:featurelist", NavigationCard.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:updateHistory")
    public List<UpdateHistory> getUpdateHistory() {
        return getChildBeansByName("govscot:updateHistory", UpdateHistory.class);
    }
}
