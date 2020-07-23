package scot.gov.publications.util;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

public class ZipEntryUtil {

    private static final Set<String> imgTypes;

    static {
        imgTypes = new HashSet<>();
        Collections.addAll(imgTypes, "jpg", "jpeg", "gif", "png");
    }

    private ZipEntryUtil() {
        // prevent instantiation
    }

    public static boolean isJson(ZipEntry entry) {
        return entry.getName().endsWith(".json");
    }

    public static boolean isImg(ZipEntry entry) {
        String filename = StringUtils.substringAfterLast(entry.getName(), "/");
        if (filename.startsWith("._")) {
            return false;
        }
        String ext = StringUtils.substringAfterLast(filename, ".");
        return imgTypes.contains(ext);
    }

    public static boolean isHtml(ZipEntry entry) {
        return hasHtmlExtension(entry.getName()) && !entry.getName().startsWith("__MACOSX");
    }

    private static boolean hasHtmlExtension(String filename) {
        String lowercaseFilename = filename.toLowerCase();
        return lowercaseFilename.endsWith(".htm") || lowercaseFilename.endsWith(".html");
    }

    public static boolean isZip(ZipEntry entry) {
        return entry.getName().endsWith(".zip");
    }

}
