package scot.gov.publications.metadata;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.util.ZipEntryUtil;
import scot.gov.publications.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.stream.Collectors.toList;

/**
 * Extracts and parses the metadata file from a zipfile.
 *
 * The extractor works by expecting a single json file in the zip and then parsing it.  It will throw and exception
 * if there is not exactly one json file in the zip.
 *
 * This class will also do the following processing while extracting the metatdata:
 * - the title is sanitised
 * - the executive summary is processed as markdown
 */
public class MetadataExtractor {

    MetadataParser metadataParser = new MetadataParser();

    private ZipUtil zipUtil = new ZipUtil();

    private Parser markdownParser = Parser.builder().build();

    private HtmlRenderer markdownToHtmlRenderer = HtmlRenderer.builder().build();

    public Metadata extract(ZipFile zipFile) throws ApsZipImporterException {

        String dir = zipUtil.getDirname(zipFile);
        List<ZipEntry> jsonEntries = zipFile.stream()
                .filter(e -> e.getName().startsWith(dir))
                .filter(ZipEntryUtil::isJson)
                .collect(toList());

        if (jsonEntries.isEmpty()) {
            throw new ApsZipImporterException("No metadata file in zip");
        }

        if (jsonEntries.size() > 1) {
            throw new ApsZipImporterException("More than one JSON file in zip, unable to identify metadata file");
        }

        try {
            Metadata metadata = metadataParser.parse(zipFile.getInputStream(jsonEntries.get(0)));
            sanitizeData(metadata);
            return metadata;
        } catch (MetadataParserException e) {
            throw new ApsZipImporterException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ApsZipImporterException("Unable to read metadata file", e);
        }
    }

    private void sanitizeData(Metadata metadata) {
        metadata.setTitle(TitleSanitiser.sanitise(metadata.getTitle()));
        processDescriptionAsMarkdown(metadata);
    }

    private void processDescriptionAsMarkdown(Metadata metadata) {
        Node node = markdownParser.parse(metadata.getDescription());
        String html = markdownToHtmlRenderer.render(node);
        metadata.setDescription(html);
    }

}
