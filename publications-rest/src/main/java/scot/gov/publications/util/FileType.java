package scot.gov.publications.util;


import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Enum top encapsulate knowledge about file types.
 */
public enum FileType {

    PDF("pdf", "application/pdf", "pdf"),

    DOC("doc", "application/msword", "word"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "word"),

    XLS("xls", "application/vnd.ms-excel", "excel"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IconNames.EXCEL),
    XLSM("xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IconNames.EXCEL),

    PPT("ppt", "application/vnd.ms-powerpoint", IconNames.PPT),
    PPS("pps", "application/vnd.ms-powerpoint", IconNames.PPT),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", IconNames.PPT),
    PPSX("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow", IconNames.PPT),

    RTF("rtf", "application/rtf", "rtf"),

    XML("xml", "application/xml", "xml"),
    XSD("xsd", "application/xsd", "xml"),

    MD("md", "text/markdown", IconNames.FALLBACK),
    CSV("csv", "text/csv", "csv"),
    TXT("txt", "text/plain", "txt"),

    HTML("html", "text/html", IconNames.FALLBACK),

    HTM("htm", "text/html", IconNames.FALLBACK),

    // open docs
    ODT("odt", "application/vnd.oasis.opendocument.text", IconNames.ODT),
    ODS("ods", "application/vnd.oasis.opendocument.spreadsheet", IconNames.ODT),
    ODP("odp", "application/vnd.oasis.opendocument.presentation", IconNames.ODT),

    // google earth
    KML("kml", "application/vnd.google-earth.kml+xml", "geo"),
    KMZ("kmz", "application/vnd.google-earth.kmz", "geo"),

    // images
    JPG("jpg", "image/jpeg", IconNames.IMAGE),
    JPEG("jpeg", "image/jpeg", IconNames.IMAGE),
    GIF("gif", "image/gif", IconNames.IMAGE),
    PNG("png", "image/png", IconNames.IMAGE),

    JSON("json", "application/json", IconNames.FALLBACK);

    private static final Set<FileType> IMAGE_TYPES = new HashSet<>();

    static {
        Collections.addAll(IMAGE_TYPES, JPG, JPEG, GIF, PNG);

    }

    private final String extension;
    private final String mimeType;
    private final String iconName;

    FileType(String extension, String mimeType, String iconName) {
        this.extension = extension;
        this.mimeType = mimeType;
        this.iconName = iconName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getIconName() {
        return iconName;
    }

    public boolean isImage() {
        return IMAGE_TYPES.contains(this);
    }

    public String toString() {
        return name().toLowerCase();
    }

    // get the FileType for a filename
    public static FileType forFilename(String filename) {
        if (StringUtils.isBlank(filename)) {
            return null;
        }

        String extension = StringUtils.substringAfterLast(filename, ".");
        String uppercase = extension.toUpperCase();

        if (EnumUtils.isValidEnum(FileType.class, uppercase)) {
            return FileType.valueOf(uppercase);
        }

        return null;
    }

    // get the FileType for a mime type
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
}
