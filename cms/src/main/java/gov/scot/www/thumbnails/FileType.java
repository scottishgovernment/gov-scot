package gov.scot.www.thumbnails;

import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Enum top encapsulate knowledge about file types.
 */
public enum FileType {

    PDF("pdf", "application/pdf"),

    DOC("doc", "application/msword"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

    XLS("xls", "application/msexcel"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    XLSM("xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    PPT("ppt", "application/vnd.ms-powerpoint"),
    PPS("pps", "application/vnd.ms-powerpoint"),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    PPSX("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow"),

    RTF("rtf", "application/rtf"),

    XML("xml", "application/xml"),
    XSD("xsd", "application/xsd"),

    MD("md", "text/markdown"),
    CSV("csv", "text/csv"),
    TXT("txt", "text/plain"),

    HTML("html", "text/html"),

    HTM("htm", "text/html"),

    // open docs
    ODT("odt", "application/vnd.oasis.opendocument.text"),
    ODS("ods", "application/vnd.oasis.opendocument.spreadsheet"),
    ODP("odp", "odp application/vnd.oasis.opendocument.presentation"),

    // google earth
    KML("kml", "application/vnd.google-earth.kml+xml"),
    KMZ("kmz", "application/vnd.google-earth.kmz"),

    // images
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    GIF("gif", "image/gif"),
    PNG("png", "image/png"),

    JSON("json", "application/json");

    private static final Set<FileType> imageTypes = new HashSet<>();
    static {
        Collections.addAll(imageTypes, JPG, JPEG, GIF, PNG);
    }

    private final String extension;
    private final String mimeType;

    FileType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isImage() {
        return imageTypes.contains(this);
    }

    public boolean fixedThumbnail() {
        return PDF != this && !isImage();
    }

    public String toString() {
        return name().toLowerCase();
    }

    public static FileType forMimeType(String mimeType) {
        Optional<FileType> result = Arrays.stream(FileType.values())
                .filter(type -> type.getMimeType().equals(mimeType))
                        .findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    public static String iconName(FileType type) {
        switch (type) {
            case CSV:
                return "csv";

            case XLS:
            case XLSM:
            case XLSX:
                return "excel";

            case KML:
            case KMZ:
                return "geo";

            case GIF:
            case JPEG:
            case JPG:
            case PNG:
                return "image";

            case PDF:
                return "pdf";

            case PPS:
            case PPSX:
            case PPT:
            case PPTX:
                return "ppt";

            case RTF:
                return "rtf";

            case TXT:
                return "txt";

            case DOC:
            case DOCX:
                return "word";

            case XML:
            case XSD:
                return "xml";

            case ODP:
            case ODS:
            case ODT:
                return "odt";

            default:
                return "fallback";

        }
    }
}
