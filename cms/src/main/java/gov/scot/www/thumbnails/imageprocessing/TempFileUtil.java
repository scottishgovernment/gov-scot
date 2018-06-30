package gov.scot.www.thumbnails.imageprocessing;

import gov.scot.www.thumbnails.FileType;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TempFileUtil {

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
