package gov.scot.www.thumbnails.imageprocessing;

public class ImageProcessingException extends Exception {

    public ImageProcessingException(String msg) {
        super(msg);
    }

    public ImageProcessingException(String msg, Throwable t) {
        super(msg, t);
    }
}
