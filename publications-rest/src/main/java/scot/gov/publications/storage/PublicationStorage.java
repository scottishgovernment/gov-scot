package scot.gov.publications.storage;

import java.io.File;

/**
 * Durable storage for uploaded APS zips, keyed by checksum, so the exact bytes behind any import
 * can be retrieved later (e.g. for diagnosis) after the original upload's temp file is gone.
 */
public interface PublicationStorage {

    boolean isConfigured();

    void save(String checksum, File file) throws PublicationStorageException;

    /**
     * Returns a disposable local copy of the archived zip for the given checksum - the caller owns
     * this file and is responsible for deleting it once done, even across implementations backed
     * by local disk (where the canonical archived copy is never the file returned here).
     */
    File fetch(String checksum) throws PublicationStorageException;

    String describeConnection();
}
