package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoResourceBean;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:DocumentInformation")
@Node(jcrType = "govscot:DocumentInformation")
public class DocumentInformation extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:srcUrl")
    public String getSrcUrl() {
        return getSingleProperty("govscot:srcUrl");
    }

    @HippoEssentialsGenerated(internalName = "govscot:document")
    public HippoResourceBean getDocument() {
        return getBean("govscot:document", HippoResourceBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:thumbnails")
    public List<HippoResourceBean> getThumbnails() {
        return getChildBeansByName("govscot:thumbnails",
                HippoResourceBean.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:highlighted")
    public Boolean getHighlighted() {
        return getSingleProperty("govscot:highlighted");
    }

    @HippoEssentialsGenerated(internalName = "govscot:accessible")
    public Boolean getAccessible() {
        return getSingleProperty("govscot:accessible");
    }

    @HippoEssentialsGenerated(internalName = "govscot:pageCount")
    public Long getPageCount() {
        return getSingleProperty("govscot:pageCount");
    }

    @HippoEssentialsGenerated(internalName = "govscot:size")
    public Long getSize() {
        return getSingleProperty("govscot:size");
    }
}
