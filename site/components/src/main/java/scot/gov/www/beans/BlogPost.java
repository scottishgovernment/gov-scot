package scot.gov.www.beans;

import org.bloomreach.forge.feed.api.FeedType;
import org.bloomreach.forge.feed.api.annot.SyndicationElement;
import org.bloomreach.forge.feed.api.transform.CalendarToDateConverter;
import org.bloomreach.forge.feed.api.transform.PathLinkResolver;
import org.bloomreach.forge.feed.api.transform.rss.StringToDescriptionConverter;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.Calendar;

@HippoEssentialsGenerated(internalName = "govscot:BlogPost")
@Node(jcrType = "govscot:BlogPost")
public class BlogPost extends AttributableContent {

    @SyndicationElement(type = FeedType.RSS, name = "title")
    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @SyndicationElement(type = FeedType.RSS, name = "description", converter = StringToDescriptionConverter.class)
    @Override
    public String getSummary() {
        return super.getSummary();
    }

    @HippoEssentialsGenerated(internalName = "govscot:publicationDate")
    @SyndicationElement(type = FeedType.RSS, name = "pubDate", converter = CalendarToDateConverter.class)
    public Calendar getPublicationDate() {
        return getSingleProperty("govscot:publicationDate");
    }

    /**
     * Posts live at blogs/&lt;blog&gt;/&lt;year&gt;/&lt;month&gt;/&lt;day&gt;/&lt;post&gt;, which mirrors
     * this bean's own path relative to the site content root, so the relative path can be derived
     * directly rather than needing each path segment resolved separately (as the News bean does via
     * its flat govscot:slug property).
     */
    @SyndicationElement(type = FeedType.RSS, name = "link", transformer = PathLinkResolver.class)
    public String getURL() {
        String path = getPath();
        String prefix = "/content/documents/govscot/";
        return path != null && path.startsWith(prefix) ? path.substring(prefix.length()) : path;
    }

    @HippoEssentialsGenerated(internalName = "govscot:author")
    public BlogAuthor getAuthor() {
        return getLinkedBean("govscot:author", BlogAuthor.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:categories")
    public String[] getCategories() {
        return getMultipleProperty("govscot:categories");
    }

    @HippoEssentialsGenerated(internalName = "govscot:heroImage")
    public ColumnImage getHeroImage() {
        return getLinkedBean("govscot:heroImage", ColumnImage.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:heroImageAlt")
    public String getHeroImageAlt() {
        return getSingleProperty("govscot:heroImageAlt");
    }

    /**
     * Posts live at blogs/&lt;blog&gt;/&lt;year&gt;/&lt;month&gt;/&lt;day&gt;/&lt;post&gt;,
     * so the enclosing Blog's index document is four folders up.
     */
    public Blog getBlog() {
        HippoBean dayFolder = getParentBean();
        if (dayFolder == null) {
            return null;
        }
        HippoBean monthFolder = dayFolder.getParentBean();
        HippoBean yearFolder = monthFolder == null ? null : monthFolder.getParentBean();
        HippoBean blogFolder = yearFolder == null ? null : yearFolder.getParentBean();
        return blogFolder == null ? null : blogFolder.getBean("index", Blog.class);
    }

    public String getLabel() {
        return "blogpost";
    }
}
