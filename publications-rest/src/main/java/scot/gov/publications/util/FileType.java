package scot.gov.publications.util;


import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static scot.gov.publications.util.MimeTypeUtils.OPEN_SPREADSHEET;

/**
 * Enum to encapsulate knowledge about file types. This is the allowlist of file types that can be
 * uploaded as part of a publication - kept aligned with the document types actually configured in
 * Bloomreach, rather than every type the old standalone importer once accepted.
 */
public enum FileType {

    CSV("csv", "text/csv", "csv"),
    DOC("doc", "application/msword", "word"),
    DOCM("docm", "application/msword", "word"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "word"),
    HTML("html", "text/html", IconNames.FALLBACK),
    JPG("jpg", "image/jpeg", IconNames.IMAGE),
    GIF("gif", "image/gif", IconNames.IMAGE),
    MSG("html", "text/html", IconNames.FALLBACK),
    ODS("ods", "application/vnd.oasis.opendocument.spreadsheet", IconNames.ODT),
    PDF("pdf", "application/pdf", "pdf"),
    PNG("png", "image/png", IconNames.IMAGE),
    PPT("ppt", "application/vnd.ms-powerpoint", IconNames.PPT),
    PPTM("pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12", IconNames.PPT),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", IconNames.PPT),
    RTF("rtf", "application/rtf", "rtf"),
    TXT("txt", "text/plain", "txt"),
    XLS("xls", "application/vnd.ms-excel", "excel"),
    XLSB("xlsb", OPEN_SPREADSHEET, IconNames.EXCEL),
    XLSM("xlsm", OPEN_SPREADSHEET, IconNames.EXCEL),
    XLTX("xlstx", OPEN_SPREADSHEET, IconNames.EXCEL),
    XLSX("xlsx", OPEN_SPREADSHEET, IconNames.EXCEL),
    XSD("xsd", "application/xsd", IconNames.EXCEL);

    private static final Set<FileType> IMAGE_TYPES = new HashSet<>();

    static {
        Collections.addAll(IMAGE_TYPES, JPG, PNG);
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
