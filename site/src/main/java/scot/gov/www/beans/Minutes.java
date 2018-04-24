package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.Calendar;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "govscot:Minutes")
@Node(jcrType = "govscot:Minutes")
public class Minutes extends AttributableContent {
    @HippoEssentialsGenerated(internalName = "govscot:location")
    public String getLocation() {
        return getProperty("govscot:location");
    }

    @HippoEssentialsGenerated(internalName = "govscot:nextMeetingDate")
    public Calendar getNextMeetingDate() {
        return getProperty("govscot:nextMeetingDate");
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

    @HippoEssentialsGenerated(internalName = "govscot:isbn")
    public String getIsbn() {
        return getProperty("govscot:isbn");
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

    @HippoEssentialsGenerated(internalName = "govscot:attendees")
    public HippoHtml getAttendees() {
        return getHippoHtml("govscot:attendees");
    }

    @HippoEssentialsGenerated(internalName = "govscot:actions")
    public HippoHtml getActions() {
        return getHippoHtml("govscot:actions");
    }

    @HippoEssentialsGenerated(internalName = "govscot:contact")
    public HippoHtml getContact() {
        return getHippoHtml("govscot:contact");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    @HippoEssentialsGenerated(internalName = "govscot:epilogue")
    public HippoHtml getEpilogue() {
        return getHippoHtml("govscot:epilogue");
    }
}
