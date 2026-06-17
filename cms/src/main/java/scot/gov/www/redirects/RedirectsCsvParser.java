package scot.gov.www.redirects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import scot.gov.publishing.hippo.redirects.Redirect;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Parses a CSV text string into a list of {@link Redirect} objects, applying URL
 * normalisation to the {@code from} field.
 *
 * <p>Expected CSV columns: {@code from}, {@code to}, {@code description} (optional).
 * No header row.
 */
public class RedirectsCsvParser {

    public static final String GOV_SCOT_ORIGIN = "https://www.gov.scot";
    public static final String GOV_SCOT_ORIGIN_WWW2 = "https://www2.gov.scot";
    public static final Set<String> ORIGINS = Set.of(GOV_SCOT_ORIGIN, GOV_SCOT_ORIGIN_WWW2);

    public List<Redirect> parse(String csvText) throws IOException {
        try (CSVParser csvParser = new CSVParser(new StringReader(csvText), CSVFormat.DEFAULT)) {
            List<CSVRecord> records = csvParser.getRecords();
            List<Redirect> redirects = new ArrayList<>(records.size());
            for (CSVRecord record : records) {
                if (record.size() != 2) {
                    throw new IOException("Line " + record.getRecordNumber()
                            + ": expected 2 columns (from, to) but found " + record.size());
                }
                redirects.add(toRedirect(record));
            }
            return redirects;
        }
    }

    private Redirect toRedirect(CSVRecord record) {
        Redirect redirect = new Redirect();
        redirect.setFrom(normalizeFromUrl(record.get(0)));
        redirect.setTo(record.get(1));
        return redirect;
    }

    /**
     * Normalises a {@code from} URL for storage: strips the gov.scot origin if present,
     * percent-decodes the path, and removes any trailing slash.
     */
    public static String normalizeFromUrl(String url) {
        if (url == null) {
            return null;
        }
        String path = ORIGINS.stream()
                .map(origin -> StringUtils.stripEnd(origin, "/"))
                .filter(url::startsWith)
                .findFirst()
                .map(origin -> url.substring(origin.length()))
                .orElse(url);
        String normalised = decodePathSegments(path);
        return StringUtils.stripEnd(normalised, "/");
    }

    /**
     * Percent-decodes each path segment individually, preserving literal slash delimiters.
     * The {@code +} character is preserved as-is (not treated as a space).
     * Malformed {@code %xx} sequences are left unchanged.
     */
    private static String decodePathSegments(String urlPath) {
        if (urlPath == null || !urlPath.contains("%")) {
            return urlPath;
        }
        String[] segments = urlPath.split("/", -1);
        StringBuilder sb = new StringBuilder(urlPath.length());
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) {
                sb.append('/');
            }
            sb.append(decodeSegment(segments[i]));
        }
        return sb.toString();
    }

    private static String decodeSegment(String segment) {
        if (!segment.contains("%")) {
            return segment;
        }
        try {
            return URLDecoder.decode(segment.replace("+", "%2B"), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return segment;
        }
    }
}
