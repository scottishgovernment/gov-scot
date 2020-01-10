package scot.gov.www.beans;

import org.apache.commons.lang.ObjectUtils;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
        List<HippoBean> unordered = getLinkedBeans("govscot:collectionItems", HippoBean.class);
        return getOrder()
                ? sortedDocuments(unordered)
                : unordered;
    }

    @HippoEssentialsGenerated(internalName = "govscot:highlight")
    public Boolean getHighlight() {
        return getProperty("govscot:highlight");
    }

    List<HippoBean> sortedDocuments(List<HippoBean> unordered) {
        List<HippoBean> ordered = new ArrayList<>(unordered);
        Collections.sort(ordered, this::compareDateIfNoNull);
        return ordered;

    }

    private int compareDateIfNoNull(HippoBean left, HippoBean right) {
        return ObjectUtils.compare(dateToCompare(left), dateToCompare(right));
    }

    Calendar dateToCompare(HippoBean bean) {
        Calendar publicationDate = bean.getProperty("govscot:publicationDate");
        if (publicationDate != null) {
            return publicationDate;
        }

        // this bean has no publication date, default to the hippostdpubwf:lastModificationDate
        return bean.getProperty("hippostdpubwf:lastModificationDate");
    }

}
