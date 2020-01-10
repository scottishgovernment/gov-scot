package scot.gov.www.beans;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.Calendar;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;

@HippoEssentialsGenerated(internalName = "govscot:ComplexDocument2")
@Node(jcrType = "govscot:ComplexDocument2")
public class ComplexDocument2 extends AttributableContent {
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

    @HippoEssentialsGenerated(internalName = "govscot:contact")
    public HippoHtml getContact() {
        return getHippoHtml("govscot:contact");
    }

    @HippoEssentialsGenerated(internalName = "govscot:revisions")
    public HippoHtml getRevisions() {
        return getHippoHtml("govscot:revisions");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:notes")
    public HippoHtml getNotes() {
        return getHippoHtml("govscot:notes");
    }

    @HippoEssentialsGenerated(internalName = "govscot:displaPrimaryDocument")
    public boolean getDisplayPrimaryDocument() {
        return getProperty("govscot:displayPrimaryDocument");
    }

    @HippoEssentialsGenerated(internalName = "govscot:displaPrimaryDocument")
    public boolean getDisplaySupportingDocuments() {
        return getProperty("govscot:displaySupportingDocuments");
    }

    public String getLabel() {
        HstRequestContext context = RequestContextProvider.get();
        ValueList publicationValueList = SelectionUtil.getValueListByIdentifier("publicationTypes", context);
        return SelectionUtil.valueListAsMap(publicationValueList).getOrDefault(getPublicationType(), "Publication");
    }

    @HippoEssentialsGenerated(internalName = "govscot:coverimage")
    public CoverImage getCoverimage() {
        return getLinkedBean("govscot:coverimage", CoverImage.class);
    }
}
