package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.Calendar;
import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:Consultation")
@Node(jcrType = "govscot:Consultation")
public class Consultation extends Publication {
    @HippoEssentialsGenerated(internalName = "govscot:slug")
    public String getSlug() {
        return getSingleProperty("govscot:slug");
    }

    @HippoEssentialsGenerated(internalName = "govscot:displayDate")
    public Calendar getDisplayDate() {
        return getSingleProperty("govscot:displayDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:contentitemlanguage")
    public String getContentitemlanguage() {
        return getSingleProperty("govscot:contentitemlanguage");
    }

    @HippoEssentialsGenerated(internalName = "govscot:openingDate")
    public Calendar getOpeningDate() {
        return getSingleProperty("govscot:openingDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:closingDate")
    public Calendar getClosingDate() {
        return getSingleProperty("govscot:closingDate");
    }

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

    @HippoEssentialsGenerated(internalName = "govscot:reportingTags")
    public String[] getReportingTags() {
        return getMultipleProperty("govscot:reportingTags");
    }

    @HippoEssentialsGenerated(internalName = "hippostd:tags")
    public String[] getTags() {
        return getMultipleProperty("hippostd:tags");
    }

    @HippoEssentialsGenerated(internalName = "govscot:isbn")
    public String getIsbn() {
        return getSingleProperty("govscot:isbn");
    }

    @HippoEssentialsGenerated(internalName = "govscot:publicationDate")
    public Calendar getPublicationDate() {
        return getSingleProperty("govscot:publicationDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:publicationType")
    public String getPublicationType() {
        return getSingleProperty("govscot:publicationType");
    }

    @HippoEssentialsGenerated(internalName = "govscot:officialdate")
    public Calendar getOfficialdate() {
        return getSingleProperty("govscot:officialdate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:sme")
    public String getSme() {
        return getSingleProperty("govscot:sme");
    }

    @HippoEssentialsGenerated(internalName = "govscot:responseUrl")
    public String getResponseUrl() {
        return getSingleProperty("govscot:responseUrl");
    }

    @HippoEssentialsGenerated(internalName = "govscot:consultationResponseMethods")
    public List<ConsultationResponseType> getConsultationResponseMethods() {
        return getChildBeansByName("govscot:consultationResponseMethods", ConsultationResponseType.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:isOpen")
    public boolean getIsOpen() {
        return getSingleProperty("govscot:isOpen");
    }

}
