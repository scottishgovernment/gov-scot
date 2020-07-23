package scot.gov.publications.metadata;

/**
 * Eception throws by the metadata parse class.
 */
public class MetadataParserException extends Exception {

    public MetadataParserException(String msg) {
        super(msg);
    }

    public MetadataParserException(String msg, Throwable t) {
        super(msg, t);
    }
}
