package scot.gov.www.searchjournal.funnelback;

import java.security.SecureRandom;

/**
 * Wrap a funnelback implementation in order to introduce errors.
 */
public class FlakyFunnelback implements Funnelback {

    private SecureRandom secureRandom = new SecureRandom();

    private Funnelback funnelback;

    private double errorRate;

    FlakyFunnelback(Funnelback funnelback, double errorRate) {
        this.funnelback = funnelback;
        this.errorRate = errorRate;
    }

    @Override
    public void close() {
        funnelback.close();
    }

    @Override
    public void publish(String collection, String key, String html) throws FunnelbackException {
        maybeCreateError();
        funnelback.publish(collection, key, html);
    }

    @Override
    public void depublish(String collection, String key) throws FunnelbackException {
        maybeCreateError();
        funnelback.depublish(collection, key);
    }

    @Override
    public JournalPosition getJournalPosition() throws FunnelbackException {
        return funnelback.getJournalPosition();
    }

    @Override
    public void storeJournalPosition(JournalPosition position) throws FunnelbackException {
        funnelback.storeJournalPosition(position);
    }

    void maybeCreateError() throws FunnelbackException {
        if (shouldCreateError()) {
            throw new FunnelbackException("I am flakey");
        }
    }

    boolean shouldCreateError() {
        return secureRandom.nextDouble() < errorRate;
    }

}
