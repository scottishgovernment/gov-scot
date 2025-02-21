package scot.gov.www.pressreleases;

public class PressReleaseImporterException extends RuntimeException {

    public PressReleaseImporterException(String message) {
        super(message);
    }

    public PressReleaseImporterException(String message, Throwable t) {
        super(message, t);
    }

}
