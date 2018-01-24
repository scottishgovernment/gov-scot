package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoDocument;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.Calendar;

@HippoEssentialsGenerated(internalName = "govscot:contentdocument")
@Node(jcrType = "govscot:contentdocument")
public class ContentDocument extends HippoDocument {
    @HippoEssentialsGenerated(internalName = "govscot:introduction")
    public String getIntroduction() {
        return getProperty("govscot:introduction");
    }

    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:publicationdate")
    public Calendar getPublicationDate() {
        return getProperty("govscot:publicationdate");
    }
}
