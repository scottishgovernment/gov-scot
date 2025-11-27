package scot.gov.www.importer.vuelio;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import scot.gov.www.importer.domain.Media;
import scot.gov.www.importer.domain.PressRelease;
import scot.gov.www.importer.vuelio.rest.Asset;
import scot.gov.www.importer.vuelio.rest.ContentItem;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts new/speech/correspondence from the REST API to our own domain model.
 */
public class ContentConverter {

    public static final String SPEECH_NODE = "speech-statement";

    public static final String CORRESPONDENCE_NODE = "correspondence";

    private final Cleaner plainTextCleaner = new Cleaner(Safelist.simpleText());

    /**
     * Convert a ContentItem item from the REST API call to our own domain object.
     *
     * @param from ContentItem, item to convert
     * @return {PressRelease in our own domain model}
     */
    public PressRelease convert(ContentItem from) {
        PressRelease to = new PressRelease();

        to.setId(from.getId());
        to.setTitle(from.getHeadline());
        to.setSummary(cleanPlainText(from.getBoilerPlate()));
        to.setSeoName(from.getBoilerPlate());
        to.setBody(from.getCoreCopy());
        ZoneId london = ZoneId.of("Europe/London");
        to.setDateTime(from.getDisplayDate().atZone(london));
        to.setUpdatedDate(from.getDateModified().atZone(london));

        if (from.isSpeech()) {
            to.setPublicationType(SPEECH_NODE);
        }

        if (from.isCorrespondence()) {
            to.setPublicationType(CORRESPONDENCE_NODE);
        }
        convertMedia(to, from);
        convertTags(to, from);
        return to;
    }

    private void convertMedia(PressRelease to, ContentItem from) {
        List<Media> media = from.getAssets().stream()
                .map(this::convertMedia).collect(Collectors.toList());
        to.setMediaAttachments(media);
    }

    private Media convertMedia(Asset from) {
        Media to = new Media();
        to.setTitle(from.getTitle());
        to.setUrl(from.getLink());
        to.setType(from.getAssetType());
        to.setOriginalName(from.getDescription());
        return to;
    }

    private void convertTags(PressRelease to, ContentItem from) {
        to.setPolicies(from.getPolicyTags());
        to.setTopics(from.getTopicTags());
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
