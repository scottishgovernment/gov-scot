package scot.gov.www.importer.sink;

import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.forge.content.pojo.model.ContentPropertyType;
import scot.gov.www.importer.domain.Media;
import scot.gov.www.importer.domain.PressRelease;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hippoecm.repository.api.HippoNodeType.HIPPO_NAME;
import static org.hippoecm.repository.api.HippoNodeType.NT_NAMED;

public class ContentNodes {

    static final String GOVSCOT_NEWS = "govscot:News";

    static final String GOVSCOT_PUBLICATION = "govscot:Publication";

    static final String GOVSCOT_TITLE = "govscot:title";

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    Topics topics = new Topics();

    ContentNode news(PressRelease release, Session session) throws RepositoryException {
        ContentNode node = contentNode(release, session);
        node.setPrimaryType(GOVSCOT_NEWS);
        Media hero = release.getMediaAttachments().isEmpty() ? null : release.getMediaAttachments().get(0);
        List<Media> attachments = release.getMediaAttachments().size() <= 1
                ? Collections.emptyList()
                : release.getMediaAttachments().subList(1, release.getMediaAttachments().size()) ;
        if (hero != null) {
            node.addNode(externalLink("govscot:heroImage", hero));
        }

        for (Media media : attachments) {
            node.addNode(externalLink("govscot:attachments", media));
        }
        return node;
    }

    ContentNode publication(PressRelease release, String contentType, Session session) throws RepositoryException {
        ContentNode node = contentNode(release, session);
        node.setPrimaryType(contentType);
        dateProperty(node, "govscot:displayDate", release.getDateTime());
        return node;
    }

    ContentNode contentNode(PressRelease release, Session session) throws RepositoryException {
        ContentNode node = new ContentNode();
        node.setName(release.getTitle());
        node.addMixinType(NT_NAMED);
        node.setProperty(HIPPO_NAME, release.getTitle());
        node.setProperty("hippostdpubwf:lastModifiedBy", "news");
        node.setProperty("hippostdpubwf:lastModifiedBy", "news");
        node.setProperty("govscot:externalId", release.getId());
        node.setProperty("hippostd:tags", release.getTopics().toArray(new String [release.getTopics().size()]));
        node.setProperty("govscot:policyTags", release.getPolicies().toArray(new String [release.getPolicies().size()]));
        node.setProperty(GOVSCOT_TITLE, release.getTitle());
        node.setProperty("govscot:summary", release.getSummary());
        node.setProperty("govscot:seoTitle", release.getTitle());
        node.setProperty("govscot:prglooslug", release.getSeoName());
        node.setProperty("govscot:metaDescription", release.getSummary());
        node.addNode(htmlNode("govscot:content", release.getBody()));
        dateProperty(node, "govscot:publicationDate", release.getDateTime());
        dateProperty(node, "govscot:updatedDate", release.getUpdatedDate());
        topics.addTopics(release, node, session);
        return node;
    }

    void dateProperty(ContentNode node, String name, ZonedDateTime zdt) {
        String formattedDate = zdt.format(dateFormat);
        node.setProperty(name, ContentPropertyType.DATE, formattedDate);
    }

    ContentNode htmlNode(String name, String html) {
        ContentNode htmlNode = new ContentNode();
        htmlNode.setName(name);
        htmlNode.setPrimaryType("hippostd:html");
        htmlNode.setProperty("hippostd:content", html);
        return htmlNode;
    }

    ContentNode externalLink(String name, Media media) {
        ContentNode contentNode = new ContentNode();
        contentNode.setName(name);
        contentNode.setPrimaryType("govscot:ExternalLink");
        contentNode.addMixinType("hippo:container");
        contentNode.addMixinType("hippostd:relaxed");
        contentNode.setProperty("govscot:url", media.getUrl());
        contentNode.setProperty(GOVSCOT_TITLE, media.getTitle());
        return contentNode;
    }
}
