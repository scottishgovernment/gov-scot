package scot.gov.www.searchjournal.funnelback;

public class FunnelbackException extends Exception {

    public FunnelbackException(String message) {
        super(message);
    }

    public FunnelbackException(String message, Exception e) {
        super(message, e);
    }
}
