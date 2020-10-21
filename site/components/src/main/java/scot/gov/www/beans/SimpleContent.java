package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:SimpleContent")
@Node(jcrType = "govscot:SimpleContent")
public class SimpleContent extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:summary")
    public String getSummary() {
        return getSingleProperty("govscot:summary");
    }

    @HippoEssentialsGenerated(internalName = "govscot:seoTitle")
    public String getSeoTitle() {
        return getSingleProperty("govscot:seoTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:metaDescription")
    public String getMetaDescription() {
        return getSingleProperty("govscot:metaDescription");
    }

    @HippoEssentialsGenerated(internalName = "hippostd:tags")
    public String[] getTags() {
        return getMultipleProperty("hippostd:tags");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    public String getLabel() {
        return "";
    }

    @HippoEssentialsGenerated(internalName = "govscot:reportingTags")
    public String[] getReportingTags() {
        return getMultipleProperty("govscot:reportingTags");
    }

    public List<UpdateHistory> getUpdateHistory() {
        List<UpdateHistory> history = getChildBeansByName(
                "govscot:updateHistory", UpdateHistory.class);
        Collections.sort(history, new Comparator<UpdateHistory>() {
            public int compare(UpdateHistory o1, UpdateHistory o2) {
                if (o1.getLastUpdated() == null || o2.getLastUpdated() == null) {
                    return 0;
                }
                return o1.getLastUpdated().compareTo(o2.getLastUpdated());
            }
        });
        Collections.reverse(history);
        return history;
    }

}
