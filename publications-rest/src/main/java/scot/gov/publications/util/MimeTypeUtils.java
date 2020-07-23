package scot.gov.publications.util;

public class MimeTypeUtils {

    private MimeTypeUtils() {
        // prevent instantiation
    }

    public static boolean isSupportedMimeType(String filename) {
        FileType fromFileType = FileType.forFilename(filename);
        return fromFileType == null;
    }

    public static String detectContentType(String filename) {

        // first determine from the extension
        FileType fromFileType = FileType.forFilename(filename);
        if (fromFileType != null) {
            return fromFileType.getMimeType();
        }

        throw new IllegalArgumentException(String.format("Unrecognised file type %s", filename));
    }
}
