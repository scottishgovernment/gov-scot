package scot.gov.publications.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Parser for metadata file contained in an aps zip
 */
public class MetadataParser {

    public Metadata parse(InputStream in) throws MetadataParserException {
        try {
            Metadata metadata = doParse(in);
            assertRequiredFields(metadata);
            assertValidFields(metadata);
            calculateZonedDateTimes(metadata);
            return metadata;
        } catch (IOException e) {
            throw new MetadataParserException("Failed to parse metadata", e);
        }
    }

    private Metadata doParse(InputStream in) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.coercionConfigFor(LogicalType.Boolean).setCoercion(CoercionInputShape.String, CoercionAction.TryConvert);
        MetadataWrapper wrapper = om.readValue(in, MetadataWrapper.class);
        return wrapper.getMetadata();
    }

    private void assertRequiredFields(Metadata metadata) throws MetadataParserException {

        List<String> missingFields = new ArrayList<>();

        if (isBlank(metadata.getId())) {
            missingFields.add("id");
        }

        if (isBlank(metadata.getIsbn())) {
            missingFields.add("isbn");
        }

        if (isBlank(metadata.getPublicationType())) {
            missingFields.add("publicationType");
        }

        if (isBlank(metadata.getTitle())) {
            missingFields.add("title");
        }

        if (metadata.getPublicationDate() == null) {
            missingFields.add("publicationDate");
        }

        if (!missingFields.isEmpty()) {
            String missignFieldsString = missingFields.stream().collect(joining(", "));
            throw new MetadataParserException("Metadata missing required field(s): " + missignFieldsString);
        }
    }

    private void assertValidFields(Metadata metadata) throws MetadataParserException {
        // the isbn should only contain letters, numbers and hyphens
        if (!validISBN(metadata.normalisedIsbn())) {
            throw new MetadataParserException("Invalid field: isbn = " + metadata.normalisedIsbn());
        }

        if (metadata.getUpdate() != null && metadata.getUpdate().getUpdateText().isEmpty()) {
            throw new MetadataParserException("Update text cannot be empty.");
        }

        if (metadata.isConsultation() || metadata.isConsultationAnalysis()) {
            Consultation consultation = metadata.getConsultation();
            if (consultation.getOpeningDate() == null || consultation.getClosingDate() == null) {
                throw new MetadataParserException("Consultation openingDate and closingDate are required.");
            }
        }
    }

    private void calculateZonedDateTimes(Metadata metadata) {
        // the publication date and update date contained in the metadata is specified without a timezone.
        // To ensure it is published at the right time we convert this to the right timezone.
        TimeZone timezone = TimeZone.getTimeZone("Europe/London");
        metadata.setPublicationDateWithTimezone(metadata.getPublicationDate().atZone(timezone.toZoneId()));

        if (metadata.getUpdate() != null) {
            metadata.getUpdate().setLastUpdatedWithTimezone(
                    metadata.getUpdate().getLastUpdated().atZone(ZoneId.of("Europe/London")));
        }
    }

    private boolean validISBN(String isbn) {
        // a valid isbn should only contain numbers and hyphens.  However, we sometimes get isbn's from aps that
        // contain postfixes like -resonses and so we just want assert that the isbn only contains letters, number
        // and hyphens.
        return isbn.matches("^[a-zA-Z0-9\\-]+$");
    }

}
