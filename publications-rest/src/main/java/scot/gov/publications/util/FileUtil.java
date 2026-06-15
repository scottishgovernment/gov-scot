package scot.gov.publications.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class FileUtil {

    public File createTempFile(String prefix, FileType type) throws IOException {
        return createTempFile(prefix, type.getExtension());
    }

    public File createTempFile(String prefix, String extension) throws IOException {
        return File.createTempFile(prefix + "-tmp-", "." + extension);
    }

    public File createTempFile(String prefix, FileType type, InputStream content) throws IOException {
        return createTempFile(prefix, type.getExtension(), content);
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

    public String hash(File file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            try (InputStream in = new DigestInputStream(new FileInputStream(file), digest)) {
                byte[] buffer = new byte[8192];
                while (in.read(buffer) != -1) {
                    // digest updated by DigestInputStream
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }
}
