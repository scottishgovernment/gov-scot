package scot.gov.publications.hippo;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.forge.content.pojo.model.ContentProperty;
import org.onehippo.forge.content.pojo.model.ContentPropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.hippo.pages.HtmlUtil;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.repo.Publication;

import javax.jcr.RepositoryException;
import java.net.MalformedURLException;
import java.net.URL;
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
        contentNode.setPrimaryType("govscot:Publication");
        contentNode.addMixinType("hippo:named");
        contentNode.setProperty(HIPPO_NAME, publication.getTitle());
        contentNode.setProperty("govscot:slug", slug);
        contentNode.setProperty("govscot:title", title);
        contentNode.setProperty("govscot:publicationId", publication.getId());
        contentNode.setProperty("govscot:publicationFilename", publication.getFilename());
        contentNode.setProperty("govscot:publicationUsername", publication.getUsername());
        contentNode.setProperty("govscot:summary", metadata.getExecutiveSummary());
        contentNode.setProperty("govscot:seoTitle", metadata.getTitle());
        contentNode.setProperty("govscot:summary", metadata.getExecutiveSummary());
        contentNode.setProperty("govscot:metaDescription", metadata.getExecutiveSummary());
        contentNode.setProperty("govscot:notes", "");
        contentNode.addNode(htmlNode("govscot:content", metadata.getDescription()));
        contentNode.addNode(htmlNode("govscot:contact",  mailToLink(metadata.getContact().getEmail())));
        contentNode.setProperty("govscot:publicationType", HippoPaths.slugify(metadata.mappedPublicationType(), false));
        contentNode.setProperty("govscot:isbn", metadata.normalisedIsbn());

        if (isNotBlank(metadata.getUrl())) {
            contentNode.setProperty(GOVSCOT_GOVSCOTURL, oldStyleUrl(metadata));
        }
        return contentNode;
    }

    public ContentNode newPageNode(String content, int i, Map<String, String> filenameToImageId) throws ApsZipImporterException {


        Document htmlDoc = Jsoup.parse(content);
        Element mainTextDiv = htmlUtil.getMainText(htmlDoc);
        String title = TitleSanitiser.sanitise(htmlUtil.getTitle(mainTextDiv, i));

        ContentNode contentNode = new ContentNode();
        contentNode.setPrimaryType("govscot:PublicationPage");
        contentNode.addMixinType("hippo:named");
        contentNode.setProperty(HIPPO_NAME, title);
        contentNode.setProperty("govscot:title", title);


        Set<String> imageLinks = imageLinks(mainTextDiv, filenameToImageId);
        String rewrittenHtml = rewriteImageLinks(mainTextDiv.html(), imageLinks);
        ContentNode htmlNode = htmlNode("govscot:content", rewrittenHtml);
        createImageFacets(htmlNode, imageLinks, filenameToImageId);

        contentNode.addNode(htmlNode);

        setBooleanProperty(contentNode, "govscot:contentsPage", htmlUtil.isContentsPage(rewrittenHtml));

        return contentNode;
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
            Map<String, String> filenameToImageId) throws ApsZipImporterException {
        try {
            doCreateImageFacets(contentNode, imageLinks, filenameToImageId);
        } catch (RepositoryException e) {
            throw new ApsZipImporterException("Failed to create image facets", e);
        }
    }

    private void doCreateImageFacets(
        ContentNode contentNode,
        Set<String> imageLinks,
        Map<String, String> filenameToImageId) throws RepositoryException {

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
