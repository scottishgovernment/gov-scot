package scot.gov.publications.manifest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import scot.gov.publications.util.MimeTypeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

/**
 * Parse a Manifest object from an input stream.
 *
 * The manifest file is a text file where each line consists of a filename and its title separated by a colon.
 *
 * The order of the entries denotes what order the documents should appear in the publication with the first one
 * used as the hero image.
 */
public class ManifestParser {

    public Manifest parse(InputStream inputStream) throws ManifestParserException {
        if (inputStream == null) {
            throw new ManifestParserException("Input stream is null");
        }

        try {
            Manifest manifest = doParse(inputStream);
            validate(manifest);
            return manifest;
        } catch (IOException e) {
            throw new ManifestParserException("Failed to read manifest", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public ManifestParser() {
        super();
    }

    private Manifest doParse(InputStream inputStream) throws IOException {
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(inputStream));
        String line;
        Manifest manifest = new Manifest();
        while ((line = reader.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                manifest.getEntries().add(entry(line));
            }
        }
        return manifest;
    }

    private void validate(Manifest manifest) throws ManifestParserException {
        // the filenames contained in the manifest should be unique - test to see if the set if filenames is
        // the same size as the number of entries in the manifest
        Set<String> filenames = manifest
                .getEntries()
                .stream()
                .map(ManifestEntry::getFilename)
                .collect(toSet());
        if (filenames.size() != manifest.getEntries().size()) {
            throw new ManifestParserException(
                    "Filenames in the manifest must be unique: "
                            + manifest.getEntries().stream().map(ManifestEntry::getFilename).collect(joining(", ")));
        }

        // all entries in the manifest should be supported types
        Set<String> unsupported = filenames.stream().filter(MimeTypeUtils::isSupportedMimeType).collect(toSet());
        if (!unsupported.isEmpty()) {
            throw new ManifestParserException(
                    "Unsupported file types in the manifest: "
                            + unsupported.stream().collect(joining(", ")));

        }
    }


    private ManifestEntry entry(String line) {
        String filename = StringUtils.substringBefore(line, ":").trim();
        String title = StringUtils.substringAfter(line, ":").trim();
        return new ManifestEntry(filename, title);
    }
}
