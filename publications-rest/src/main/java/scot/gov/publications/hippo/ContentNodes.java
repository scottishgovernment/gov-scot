package scot.gov.publications.hippo;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.ISO8601;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.onehippo.forge.content.pojo.model.BinaryValue;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.forge.content.pojo.model.ContentProperty;
import org.onehippo.forge.content.pojo.model.ContentPropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.hippo.pages.HtmlUtil;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.repo.Publication;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hippoecm.repository.api.HippoNodeType.HIPPO_NAME;
import static scot.gov.publications.hippo.Constants.*;

public class ContentNodes {

    private static final Logger LOG = LoggerFactory.getLogger(ContentNodes.class);

    HtmlUtil htmlUtil = new HtmlUtil();

    public ContentNode newContentNode(Metadata metadata, Publication publication, String slug) {
        String title = TitleSanitiser.sanitise(publication.getTitle());
        ContentNode contentNode = new ContentNode();
        contentNode.setPrimaryType(metadata.isConsultation() ? "govscot:Consultation" : "govscot:Publication");
        contentNode.addMixinType(HIPPO_NAMED);
        contentNode.setProperty(HIPPO_NAME, publication.getTitle());
        contentNode.setProperty("govscot:slug", slug);
        contentNode.setProperty(GOVSCOT_TITLE, title);
        contentNode.setProperty("govscot:publicationId", publication.getId());
        contentNode.setProperty("govscot:publicationFilename", publication.getFilename());
        contentNode.setProperty("govscot:publicationUsername", publication.getUsername());
        contentNode.setProperty("govscot:seoTitle", metadata.getTitle());
        contentNode.setProperty(GOVSCOT_SUMMARY, metadata.getExecutiveSummary());
        contentNode.setProperty("govscot:metaDescription", metadata.getExecutiveSummary());
        contentNode.setProperty("govscot:notes", "");
        contentNode.addNode(htmlNode(GOVSCOT_CONTENT, metadata.getDescription()));
        contentNode.addNode(htmlNode("govscot:contact",  mailToLink(metadata.getContact().getEmail())));
        contentNode.setProperty("govscot:publicationType", HippoPaths.slugify(metadata.mappedPublicationType(), false));
        contentNode.setProperty("govscot:isbn", metadata.normalisedIsbn());
        setPublicationDates(contentNode, metadata);

        if (isNotBlank(metadata.getUrl())) {
            contentNode.setProperty(GOVSCOT_GOVSCOTURL, oldStyleUrl(metadata));
        }
        return contentNode;
    }

    /**
     * Refresh an existing publication's exported ContentNode from a re-imported zip's metadata.
     * Title, summary, seoTitle, metaDescription, notes and the description body are user-editable
     * in the CMS, so they are only set if not already present - preserving any edits made since
     * the last import, matching the behaviour of the publications-service importer. Contact, isbn
     * and the old-style url are always refreshed from the zip. Never adds/removes child nodes
     * already present on the exported tree (see the content-exim binder note on
     * updateMirrorDocbase in PublicationNodeUpdater).
     */
    public void updateContentNode(ContentNode contentNode, Metadata metadata) {
        String title = TitleSanitiser.sanitise(metadata.getTitle());
        setPropertyIfAbsent(contentNode, HIPPO_NAME, metadata.getTitle());
        setPropertyIfAbsent(contentNode, GOVSCOT_TITLE, title);
        setPropertyIfAbsent(contentNode, "govscot:seoTitle", metadata.getTitle());
        setPropertyIfAbsent(contentNode, GOVSCOT_SUMMARY, metadata.getExecutiveSummary());
        setPropertyIfAbsent(contentNode, "govscot:metaDescription", metadata.getExecutiveSummary());
        setPropertyIfAbsent(contentNode, "govscot:notes", "");
        addHtmlNodeIfAbsent(contentNode, GOVSCOT_CONTENT, metadata.getDescription());
        updateHtmlNodeContent(contentNode, "govscot:contact", mailToLink(metadata.getContact().getEmail()));
        contentNode.setProperty("govscot:isbn", metadata.normalisedIsbn());
        setPublicationDates(contentNode, metadata);

        if (isNotBlank(metadata.getUrl())) {
            contentNode.setProperty(GOVSCOT_GOVSCOTURL, oldStyleUrl(metadata));
        }
    }

    private void setPublicationDates(ContentNode contentNode, Metadata metadata) {
        String publicationDate = ISO8601.format(GregorianCalendar.from(metadata.getPublicationDateWithTimezone()));
        contentNode.setProperty("govscot:publicationDate", ContentPropertyType.DATE, publicationDate);
        contentNode.setProperty("govscot:displayDate", ContentPropertyType.DATE, publicationDate);
    }

    private void setPropertyIfAbsent(ContentNode contentNode, String name, String value) {
        if (!contentNode.hasProperty(name)) {
            contentNode.setProperty(name, value);
        }
    }

    private void addHtmlNodeIfAbsent(ContentNode contentNode, String name, String html) {
        if (!contentNode.hasNode(name)) {
            contentNode.addNode(htmlNode(name, html));
        }
    }

    private void updateHtmlNodeContent(ContentNode contentNode, String name, String html) {
        if (contentNode.hasNode(name)) {
            contentNode.getNode(name).setProperty("hippostd:content", html);
        } else {
            contentNode.addNode(htmlNode(name, html));
        }
    }

    public ContentNode newPageNode(String content, int i, Map<String, String> filenameToImageId) throws ApsZipImporterException {


        Document htmlDoc = Jsoup.parse(content);
        Element mainTextDiv = htmlUtil.getMainText(htmlDoc);
        String title = TitleSanitiser.sanitise(htmlUtil.getTitle(mainTextDiv, i));

        ContentNode contentNode = new ContentNode();
        contentNode.setPrimaryType("govscot:PublicationPage");
        contentNode.addMixinType(HIPPO_NAMED);
        contentNode.setProperty(HIPPO_NAME, title);
        contentNode.setProperty(GOVSCOT_TITLE, title);


        Set<String> imageLinks = imageLinks(mainTextDiv, filenameToImageId);
        String rewrittenHtml = rewriteImageLinks(mainTextDiv.html(), imageLinks);
        ContentNode htmlNode = htmlNode(GOVSCOT_CONTENT, rewrittenHtml);
        createImageFacets(htmlNode, imageLinks, filenameToImageId);

        contentNode.addNode(htmlNode);

        setBooleanProperty(contentNode, "govscot:contentsPage", htmlUtil.isContentsPage(rewrittenHtml));

        return contentNode;
    }

    /**
     * Build the "How to respond" page added to every consultation paper, containing standard
     * boilerplate text about how responses are handled rather than content from the APS zip.
     */
    public ContentNode newConsultationResponseNode(String html) {
        String title = "How to respond";

        ContentNode contentNode = new ContentNode();
        contentNode.setPrimaryType("govscot:PublicationPage");
        contentNode.addMixinType(HIPPO_NAMED);
        contentNode.setProperty(HIPPO_NAME, title);
        contentNode.setProperty(GOVSCOT_TITLE, title);
        contentNode.addNode(htmlNode(GOVSCOT_CONTENT, html));
        setBooleanProperty(contentNode, "govscot:contentsPage", false);
        return contentNode;
    }

    public ContentNode newDocumentInfoNode(
            String title,
            long pageCount,
            String filename,
            String mimeType,
            BinaryValue document) {

        ContentNode contentNode = new ContentNode();
        contentNode.setPrimaryType("govscot:DocumentInformation");
        contentNode.addMixinType(HIPPO_NAMED);
        contentNode.setProperty(HIPPO_NAME, title);
        contentNode.setProperty(GOVSCOT_TITLE, title);
        setBooleanProperty(contentNode, "govscot:accessible", false);
        setBooleanProperty(contentNode, "govscot:highlighted", false);
        contentNode.setProperty("govscot:pageCount", ContentPropertyType.LONG, Long.toString(pageCount));
        contentNode.addNode(documentResourceNode(filename, mimeType, document));
        return contentNode;
    }

    private ContentNode documentResourceNode(String filename, String mimeType, BinaryValue document) {
        ContentNode resourceNode = new ContentNode();
        resourceNode.setName("govscot:document");
        resourceNode.setPrimaryType("hippo:resource");
        resourceNode.setProperty(HIPPO_FILENAME, filename);
        resourceNode.setProperty(JCR_MIMETYPE, mimeType);
        resourceNode.setProperty("jcr:data", document);
        resourceNode.setProperty("jcr:lastModified", ContentPropertyType.DATE, ISO8601.format(Calendar.getInstance()));
        // avoid costly text extraction
        resourceNode.setProperty("hippo:text", "");
        return resourceNode;
    }

    void setBooleanProperty(ContentNode contentNode, String name, boolean val) {
        ContentProperty prop = new ContentProperty(name, ContentPropertyType.BOOLEAN);
        prop.setValue(Boolean.toString(val));
        contentNode.setProperty(prop);
    }

    private Set<String> imageLinks(Element div, Map<String, String> filenameToImageId) {
        return div
                .select("img")
                .stream()
                .map(el -> el.attr("src"))
                .filter(StringUtils::isNotBlank)
                .filter(src -> filenameToImageId.containsKey(src))
                .collect(toSet());
    }

    private String rewriteImageLinks(String html, Set<String> imageLinks) {
        String rewrittenContent = html;
        for (String imageLink : imageLinks) {
            String from = imageLink;
            String to = String.format("%s/{_document}/hippogallery:original", imageLink);
            rewrittenContent = rewrittenContent.replaceAll(from, to);
        }
        return rewrittenContent;
    }

    private void createImageFacets(
            ContentNode contentNode,
            Set<String> imageLinks,
            Map<String, String> filenameToImageId)  {

        // create facets for each of the images we know about
        Set<String> imageNames = imageLinks.stream()
                .filter(filenameToImageId::containsKey)
                .collect(toSet());
        for (String imageName : imageNames) {
            ContentNode imageLink = new ContentNode();
            imageLink.setName(imageName);
            imageLink.setPrimaryType("hippogallerypicker:imagelink");
            imageLink.setProperty("hippo:docbase", filenameToImageId.get(imageName));
            imageLink.setProperty("hippo:facets", new String[]{});
            imageLink.setProperty("hippo:modes", new String[]{});
            imageLink.setProperty("hippo:values", new String[]{});
            contentNode.addNode(imageLink);
        }
    }

    private String oldStyleUrl(Metadata metadata) {
        // we only want to store the path of the old style url
        try {
            return new URL(metadata.getUrl()).getPath();
        } catch (MalformedURLException e) {
            LOG.warn("Cannot parse metadata url: {}", metadata.getUrl(), e);
            return "";
        }
    }

    ContentNode htmlNode(String name, String html) {
        ContentNode htmlNode = new ContentNode();
        htmlNode.setName(name);
        htmlNode.setPrimaryType("hippostd:html");
        htmlNode.setProperty("hippostd:content", html);
        return htmlNode;
    }

    private String mailToLink(String email) {
        return isBlank(email)
                ? ""
                : String.format("<p>Email: <a href=\"mailto:%s\">%s</a></p>", email, email);
    }
}
