package scot.gov.www.beans;

import org.hippoecm.hst.container.RequestContextProvider;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.List;
import java.util.Map;

import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;
import scot.gov.www.beans.CollectionGroup;

@HippoEssentialsGenerated(internalName = "govscot:Collection")
@Node(jcrType = "govscot:Collection")
public class Collection extends AttributableContent {
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

    @HippoEssentialsGenerated(internalName = "govscot:contact")
    public HippoHtml getContact() {
        return getHippoHtml("govscot:contact");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    @HippoEssentialsGenerated(internalName = "govscot:groups")
    public List<CollectionGroup> getGroups() {
        return getChildBeansByName("govscot:groups", CollectionGroup.class);
    }

    public String getLabel() {
        return "Collection";
    }
}
