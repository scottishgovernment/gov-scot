package scot.gov.publications;

public class ApsZipImporterException extends Exception {

    public ApsZipImporterException(String msg) {
        super(msg);
    }

    public ApsZipImporterException(String msg, Exception e) {
        super(msg, e);
    }
}
