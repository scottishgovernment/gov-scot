package scot.gov.publications.manifest;

/**
 * Exception throws in the case where the manifest file cannot be parsed.
 */
public class ManifestParserException extends Exception {

    public ManifestParserException(String msg) {
        super(msg);
    }

    public ManifestParserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
