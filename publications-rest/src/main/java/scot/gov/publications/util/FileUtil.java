package scot.gov.publications.util;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class FileUtil {

    public File createTempFile(String prefix, String extension) throws IOException {
        return File.createTempFile(prefix + "-tmp-", "." + extension);
    }

    public File createTempFile(String prefix, String extenstion, InputStream content) throws IOException {
        File file = createTempFile(prefix, extenstion);
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
