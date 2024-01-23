package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "govscot:SearchResultsPage")
@Node(jcrType = "govscot:SearchResultsPage")
public class SearchResultsPage extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:noResultsMessage")
    public HippoHtml getNoResultsMessage() {
        return getHippoHtml("govscot:noResultsMessage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:blankSearchQueryMessage")
    public HippoHtml getBlankSearchQueryMessage() {
        return getHippoHtml("govscot:blankSearchQueryMessage");
    }
}
