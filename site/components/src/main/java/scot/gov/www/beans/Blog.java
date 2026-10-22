package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "govscot:Blog")
@Node(jcrType = "govscot:Blog")
public class Blog extends AttributableContent {

    @HippoEssentialsGenerated(internalName = "govscot:category")
    public String[] getCategory() {
        return getMultipleProperty("govscot:category");
    }

    /**
     * The blog's index document lives in a folder named after the blog (e.g. blogs/digital-directorate),
     * which is also the name used for the blog's RSS feed descriptor under /content/documents/govscot/feed.
     */
    public String getSlug() {
        HippoBean folder = getParentBean();
        return folder == null ? null : folder.getName();
    }

    @HippoEssentialsGenerated(internalName = "govscot:ContactInformation")
    public ContactInformation getContactInformation() {
        return getBean("govscot:ContactInformation", ContactInformation.class);
    }

    public String getLabel() {
        return "blog";
    }
}
