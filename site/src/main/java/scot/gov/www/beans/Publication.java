package scot.gov.www.beans;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;

import java.util.*;

@HippoEssentialsGenerated(internalName = "govscot:Publication")
@Node(jcrType = "govscot:Publication")
public class Publication extends AttributableContent {
    @HippoEssentialsGenerated(internalName = "govscot:isbn")
    public String getIsbn() {
        return getProperty("govscot:isbn");
    }

    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:summary")
    public String getSummary() {
        return getProperty("govscot:summary");
    }

    @HippoEssentialsGenerated(internalName = "govscot:seoTitle")
    public String getSeoTitle() {
        return getProperty("govscot:seoTitle");
    }

    @HippoEssentialsGenerated(internalName = "govscot:metaDescription")
    public String getMetaDescription() {
        return getProperty("govscot:metaDescription");
    }

    @HippoEssentialsGenerated(internalName = "hippostd:tags")
    public String[] getTags() {
        return getProperty("hippostd:tags");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    @HippoEssentialsGenerated(internalName = "govscot:publicationDate")
    public Calendar getPublicationDate() {
        return getProperty("govscot:publicationDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:publicationType")
    public String getPublicationType() {
        return getProperty("govscot:publicationType");
    }

    @HippoEssentialsGenerated(internalName = "govscot:officialdate")
    public Calendar getOfficialdate() {
        return getProperty("govscot:officialdate");
    }

    public String getLabel() {
        final ValueList publicationValueList = SelectionUtil
                .getValueListByIdentifier("publicationTypes",
                        RequestContextProvider.get());
        Map publicationMap = SelectionUtil.valueListAsMap(publicationValueList);
        return publicationMap.get(this.getPublicationType()).toString();
    }

    @HippoEssentialsGenerated(internalName = "govscot:contact")
    public HippoHtml getContact() {
        return getHippoHtml("govscot:contact");
    }

    @HippoEssentialsGenerated(internalName = "govscot:executiveSummary")
    public HippoHtml getExecutiveSummary() {
        return getHippoHtml("govscot:executiveSummary");
    }

    @HippoEssentialsGenerated(internalName = "govscot:epilogue")
    public HippoHtml getEpilogue() {
        return getHippoHtml("govscot:epilogue");
    }

    public List<UpdateHistory> getUpdateHistory() {
        List<UpdateHistory> history = getChildBeansByName("govscot:updateHistory", UpdateHistory.class);
        Collections.sort(history, new Comparator<UpdateHistory>() {
            public int compare(UpdateHistory o1, UpdateHistory o2) {
                if (o1.getLastUpdated() == null || o2.getLastUpdated() == null)
                    return 0;
                return o1.getLastUpdated().compareTo(o2.getLastUpdated());
            }
        });
        Collections.reverse(history);
        return history;
    }

}
