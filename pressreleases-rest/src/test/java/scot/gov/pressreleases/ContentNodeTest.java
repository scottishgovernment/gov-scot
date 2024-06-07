package scot.gov.pressreleases;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onehippo.forge.content.pojo.model.ContentNode;
import scot.gov.pressreleases.domain.Contact;
import scot.gov.pressreleases.domain.Media;
import scot.gov.pressreleases.domain.PressRelease;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.hippoecm.repository.api.HippoNodeType.NT_NAMED;
import static org.junit.Assert.*;

public class ContentNodeTest {

    ContentNodes sut = new ContentNodes();

    @Before
    public void init() {
        // Topics has it own unit tests
        sut.topics = Mockito.mock(Topics.class);
    }

    @Test
    public void expectedContentNodeForNews() throws RepositoryException {

        // ARRANGE
        PressRelease pressRelease = newsItemAllFields();
        Session session = Mockito.mock(Session.class);

        // ACT
        ContentNode contentNode = sut.news(pressRelease, session);

        // ASSERT
        assertEquals("govscot:News", contentNode.getPrimaryType());
        assertCoreFields(pressRelease, contentNode);
    }

    @Test
    public void expectedContentNodeForSpeech() throws RepositoryException {

        // ARRANGE
        PressRelease pressRelease = speechItemAllFields();
        Session session = Mockito.mock(Session.class);

        // ACT
        ContentNode contentNode = sut.publication(pressRelease, "govscot:SpeechOrStatement", session);

        // ASSERT
        assertEquals("govscot:SpeechOrStatement", contentNode.getPrimaryType());
        assertCoreFields(pressRelease, contentNode);
        assertEquals(new ContentNodes().dateFormat.format(pressRelease.getDateTime()), contentNode.getProperty("govscot:displayDate").getValue());
    }


    @Test
    public void expectedContentNodeForNewsWithSingleMediaItem() throws RepositoryException {

        // ARRANGE - a single media item
        PressRelease pressRelease = newsItemAllFields();
        pressRelease.setMediaAttachments(Arrays.asList(media("one")));
        Session session = Mockito.mock(Session.class);

        // ACT
        ContentNode contentNode = sut.news(pressRelease, session);

        // ASSERT - one media item should have been used as the hero image
        Assert.assertNotNull(contentNode.getNode("govscot:heroImage"));
        ContentNode hero = contentNode.getNode("govscot:heroImage");
        assertEquals(pressRelease.getMediaAttachments().get(0).getTitle(), hero.getProperty("govscot:title").getValue());
        assertEquals(pressRelease.getMediaAttachments().get(0).getUrl(), hero.getProperty("govscot:url").getValue());
    }

    @Test
    public void expectedContentNodeForNewsWithMultipleMediaItem() throws RepositoryException {

        // ARRANGE - a single media item
        PressRelease pressRelease = newsItemAllFields();
        pressRelease.setMediaAttachments(Arrays.asList(media("one"), media("two")));
        Session session = Mockito.mock(Session.class);

        // ACT
        ContentNode contentNode = sut.news(pressRelease, session);

        // ASSERT - one media item should have been used as the hero image
        Assert.assertNotNull(contentNode.getNode("govscot:heroImage"));
        ContentNode hero = contentNode.getNode("govscot:heroImage");
        assertEquals(pressRelease.getMediaAttachments().get(0).getTitle(), hero.getProperty("govscot:title").getValue());
        assertEquals(pressRelease.getMediaAttachments().get(0).getUrl(), hero.getProperty("govscot:url").getValue());
        List<ContentNode> attachments = contentNode.getNodes().stream().filter(n -> n.getName().equals("govscot:attachments")).collect(toList());
        assertTrue(attachments.size() == 1);
        assertEquals(pressRelease.getMediaAttachments().get(1).getTitle(), attachments.get(0).getProperty("govscot:title").getValue());
        assertEquals(pressRelease.getMediaAttachments().get(1).getUrl(), attachments.get(0).getProperty("govscot:url").getValue());
    }

    void assertCoreFields(PressRelease release, ContentNode node) {
        assertEquals(release.getTitle(), node.getName());
        Assert.assertTrue(node.getMixinTypes().contains(NT_NAMED));
        assertEquals(release.getId(), node.getProperty("govscot:externalId").getValue());

        // first media is the heo image

        // second and third ones are attachments

//        node.setName(release.getTitle());
//        node.addMixinType(NT_NAMED);
//        node.setProperty(HIPPO_NAME, release.getTitle());
//        node.setProperty("hippostdpubwf:lastModifiedBy", "news");
//        node.setProperty("hippostdpubwf:lastModifiedBy", "news");
//        node.setProperty("govscot:externalId", release.getId());
//        node.setProperty("hippostd:tags", release.getTopics().values().toArray(new String [release.getTopics().size()]));
//        node.setProperty("govscot:policyTags", release.getPolicies().toArray(new String [release.getPolicies().size()]));
//        node.setProperty(GOVSCOT_TITLE, release.getTitle());
//        node.setProperty("govscot:summary", release.getSummary());
//        node.setProperty("govscot:seoTitle", release.getTitle());
//        node.setProperty("govscot:prglooslug", release.getSeoName());
//        node.setProperty("govscot:metaDescription", release.getSummary());
//        node.addNode(htmlNode("govscot:background", release.getNotesToEditors()));
//        node.addNode(htmlNode("govscot:content", release.getBody()));
//        dateProperty(node, "govscot:publicationDate", release.getDateTime());
//        dateProperty(node, "govscot:updatedDate", release.getUpdatedDate());
//        topics.addTopics(release, node, session);
    }

    @Test
    public void expectedContentNodeForCorrespondence() throws RepositoryException {
    }

    @Test
    public void emptyAttachmentsAsExpected() throws RepositoryException {
    }


    @Test
    public void noExternalLinkHandledAsdExpected() throws RepositoryException {
    }


    PressRelease newsItemAllFields() {
        return pressRelease();
    }

    PressRelease speechItemAllFields() {
        return pressRelease();
    }


    PressRelease pressRelease() {
        PressRelease release = new PressRelease();
        release.setId("id");
        release.setTitle("title");
        release.setSummary("summary");
        release.setBody("body");
        release.setNotesToEditors("notes");
        release.setDateTime(knowDateTime());
        release.setSeoName("seoname");
        release.setClient("client");
        release.setContacts(Arrays.asList(contact()));
        release.setPolicies(Arrays.asList("policy1", "policy2"));
        release.setTopics(Collections.emptyMap());
        release.setMediaAttachments(Collections.emptyList());
        release.setUpdatedDate(knowDateTime());
        release.setPublicationType("speech-statement");
        return release;
    }

    Contact contact() {
        Contact contact = new Contact();
        contact.setEmailAddress("email");
        contact.setFirstName("firstname");
        contact.setLastName("lastname");
        return contact;
    }

    Media media(String prefix) {
        Media media = new Media();
        media.setTitle(prefix + "title");
        media.setType(prefix + "meditype");
        media.setUrl(prefix + "mediaurl");
        media.setOriginalName(prefix + "mediaorigname");
        return media;
    }

    ZonedDateTime knowDateTime() {
        return ZonedDateTime.of(2020, 1, 1, 1, 1, 1, 0, ZoneId.systemDefault());
    }
}
