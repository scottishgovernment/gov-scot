package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.List;
import scot.gov.www.beans.AdditionalContent;

@HippoEssentialsGenerated(internalName = "govscot:Topic")
@Node(jcrType = "govscot:Topic")
public class Topic extends SimpleContent {
    @HippoEssentialsGenerated(internalName = "govscot:featuredItems")
    public List<HippoBean> getFeaturedItems() {
        return getLinkedBeans("govscot:featuredItems", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:responsibleRole")
    public List<HippoBean> getResponsibleRoles() {
        return getLinkedBeans("govscot:responsibleRole", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:image")
    public BannerImages getImage() {
        return getLinkedBean("govscot:image", BannerImages.class);
    }

    public String getLabel() { return ""; }

    @HippoEssentialsGenerated(internalName = "govscot:sme")
    public String getSme() {
        return getProperty("govscot:sme");
    }

    @HippoEssentialsGenerated(internalName = "govscot:AdditionalContent")
    public List<AdditionalContent> getAdditionalContent() {
        return getChildBeansByName("govscot:AdditionalContent",
                AdditionalContent.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:TrailingContent")
    public List<AdditionalContent> gettrailingContent() {
        return getChildBeansByName("govscot:TrailingContent",
                AdditionalContent.class);
    }

}
