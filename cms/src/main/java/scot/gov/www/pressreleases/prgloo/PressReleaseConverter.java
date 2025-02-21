package scot.gov.www.pressreleases.prgloo;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import scot.gov.www.pressreleases.domain.Contact;
import scot.gov.www.pressreleases.domain.Media;
import scot.gov.www.pressreleases.domain.PressRelease;
import scot.gov.www.pressreleases.prgloo.rest.Attachment;
import scot.gov.www.pressreleases.prgloo.rest.Classification;
import scot.gov.www.pressreleases.prgloo.rest.PressReleaseItem;
import scot.gov.www.pressreleases.prgloo.rest.Sender;
import scot.gov.www.pressreleases.prgloo.rest.TagGroup;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;
import static scot.gov.www.pressreleases.prgloo.PRGlooClient.*;

/**
 * Converts press releases from the SOAP web service to our own domain model.
 */
public class PressReleaseConverter {

    public static final String SPEECH_NODE = "speech-statement";

    public static final String CORRESPONDENCE_NODE = "correspondence";


    private final Cleaner plainTextCleaner = new Cleaner(Safelist.simpleText());

    String prglooMediaUrl = "https://cdn.prgloo.com/media/";

    /**
     * Convert a PressReleaseItems item from a SOAP API call to our own domain object.
     *
     * @param from PressReleaseItems item to convert
     * @return {PressRelease in our own domain model}
     */
    public PressRelease convert(PressReleaseItem from) {
        PressRelease to = new PressRelease();

        to.setId(from.getId());
        to.setTitle(from.getTitle());
        to.setSummary(cleanPlainText(from.getSummary()));
        to.setSeoName(from.getSeoName());
        to.setBody(from.getBody());
        ZoneId london = ZoneId.of("Europe/London");
        to.setDateTime(from.getPublishedFrom().atZoneSameInstant(london));
        to.setUpdatedDate(from.getUpdated().atZoneSameInstant(london));
        to.setNewsAreaId(from.getContentStreamId());

        if (from.getContentStreamId().equals(PRESS_RELEASE_STREAM_ID)) {
            convertContact(to, from.getSender());
        }

        if (from.getContentStreamId().equals(SPEECH_STREAM_ID)) {
            to.setPublicationType(SPEECH_NODE);
        }

        if (from.getContentStreamId().equals(CORRESPONDENCE_STREAM_ID)) {
            to.setPublicationType(CORRESPONDENCE_NODE);
        }
        convertMedia(to, from);
        convertTags(to, from);
        return to;
    }

    private void convertContact(PressRelease to, Sender from) {
        Contact contact = new Contact();

        contact.setLastName(from.getName().substring(from.getName().indexOf(' ') + 1));
        contact.setFirstName(from.getName().substring(0, from.getName().indexOf(' ') + 1).trim());
        contact.setEmailAddress(from.getEmail());
        contact.setPhone(from.getLandline());
        to.setContacts(singletonList(contact));
    }

    private void convertMedia(PressRelease to, PressReleaseItem from) {
        List<Media> attachments = new ArrayList<>();
        if (from.getHeroImage() != null) {
            attachments.add(convertMedia(from.getHeroImage()));
        }
        attachments.addAll(from.getAttachments().stream()
                .map(this::convertMedia)
                .collect(toList()));
        attachments.addAll(from.getEmbeddedResources().stream()
                .map(this::convertMedia)
                .collect(toList()));
        to.setMediaAttachments(attachments);
    }

    private Media convertMedia(Attachment from) {
        scot.gov.www.pressreleases.domain.Media to = new scot.gov.www.pressreleases.domain.Media();
        to.setTitle(from.getName());
        to.setUrl(prglooMediaUrl + from.getPublicId());
        to.setType(from.getContentType());
        to.setOriginalName(from.getDescription());
        return to;
    }

    private void convertTags(PressRelease to, PressReleaseItem from) {
        List<String> policies = getPolicyTags(from);
        to.setPolicies(policies);
        Map<String, String> topics = getTopicTags(from);
        to.setTopics(topics);
    }

    private List<String> getPolicyTags(PressReleaseItem from) {
        Set<String> set = from.getTagGroups().stream()
                .filter(g -> g.getName().toLowerCase().startsWith("policy"))
                .flatMap(g -> g.getTags().stream())
                .map(Classification::getId)
                .collect(toSet());
        List<String> result = new ArrayList<>(set);
        result.sort(null);
        return result;
    }

    private Map<String, String> getTopicTags(PressReleaseItem from) {
        return from.getTagGroups().stream()
                .filter(this::isTopicTagGroup)
                .sorted()
                .flatMap(g -> g.getTags().stream())
                .collect(toMap(Classification::getId, Classification::getName));
    }

    /**
     * See http://jira.digital.gov.uk/browse/MGS-7186
     * PRGloo changed the tag group used for topics from
     * "Topics (for gov.scot site - select all that apply)"
     * to
     * "Website Categories (for gov.scot site - select all that apply)"
     */
    boolean isTopicTagGroup(TagGroup tagGroup) {
        String lowercaseName = tagGroup.getName().toLowerCase();
        return StringUtils.startsWithAny(lowercaseName, "website categories");
    }

    private String cleanPlainText(String s) {
        if (s == null) {
            return s;
        }
        Document doc = Jsoup.parse(Jsoup.parse(s).text());
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        return plainTextCleaner.clean(doc).body().text();
    }

}
