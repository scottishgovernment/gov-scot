package scot.gov.publications.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Fallback {@link PublicationStorage} for environments where S3 archiving is not configured (e.g.
 * local dev) - stores uploaded zips on the local filesystem, outside the per-upload temp file, so
 * the archived zip survives independently of that temp file being cleaned up.
 */
public class LocalFilePublicationStorage implements PublicationStorage {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFilePublicationStorage.class);

    private final File directory;

    public LocalFilePublicationStorage() {
        this(new File(System.getProperty("java.io.tmpdir"), "publications-storage"));
    }

    LocalFilePublicationStorage(File directory) {
        this.directory = directory;
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    @Override
    public void save(String checksum, File file) throws PublicationStorageException {
        try {
            Files.createDirectories(directory.toPath());
            File target = target(checksum);
            Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LOG.info("Stored uploaded zip locally: {}", target.getAbsolutePath());
        } catch (IOException e) {
            throw new PublicationStorageException("Failed to store zip on local disk", e);
        }
    }

    /**
     * Returns a disposable copy of the stored zip, not the canonical archived file itself - callers
     * are expected to delete whatever fetch() returns once done with it (see PublicationStorage),
     * and the one archived copy under {@link #directory} must survive that.
     */
    @Override
    public File fetch(String checksum) throws PublicationStorageException {
        File target = target(checksum);
        if (!target.exists()) {
            throw new PublicationStorageException("No locally stored zip found for checksum=" + checksum, null);
        }
        try {
            File copy = File.createTempFile("publication-fetch-", ".zip");
            Files.copy(target.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return copy;
        } catch (IOException e) {
            throw new PublicationStorageException("Failed to copy locally stored zip for checksum=" + checksum, e);
        }
    }

    @Override
    public String describeConnection() {
        return "local file storage: " + directory.getAbsolutePath();
    }

    private File target(String checksum) {
        return new File(directory, checksum + ".zip");
    }
}
