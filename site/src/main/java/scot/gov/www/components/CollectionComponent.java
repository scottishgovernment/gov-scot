package scot.gov.www.components;


import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import scot.gov.www.beans.Collection;
import scot.gov.www.beans.CollectionGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class CollectionComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        HstRequestContext context = request.getRequestContext();
        Collection document = context.getContentBean(Collection.class);
        request.setAttribute("document", document);

        for (CollectionGroup group : document.getGroups()) {
            // if a group is set to be ordered by date, check if a date has been set for each item
            if (group.getOrder()){
                ArrayList<HippoBean> orderedItems = new ArrayList<>(group.getCollectionItems());
                Collections.sort(orderedItems, this::compareDateIfNoNull);
                Collections.reverse(orderedItems);
                group.setOrderedItems(orderedItems);
            }
        }

    }

    private int compareDateIfNoNull(HippoBean left, HippoBean right) {
        return dateToCompare(left).compareTo(dateToCompare(right));
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



