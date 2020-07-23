package scot.gov.imageprocessing;

import org.apache.commons.io.IOUtils;
import scot.gov.imageprocessing.thumbnails.FileType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TempFileUtil {

    private TempFileUtil() {
        // static only class
    }

    public static File createTempFile(FileType type) throws IOException {
        return File.createTempFile("thumbnails-tmp-", "." + type.getExtension());
    }

    public static File createTempFile(FileType type, InputStream content) throws IOException {
        File file = createTempFile(type);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            IOUtils.copy(content, stream);
            return file;
        } finally {
            IOUtils.closeQuietly(content);
            IOUtils.closeQuietly(stream);
        }
    }
}
