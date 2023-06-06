package scot.gov.www.searchjournal.funnelback;

public interface Funnelback {

    void close();

    void publish(String collection, String key, String html) throws FunnelbackException;

    void depublish(String collection, String key) throws FunnelbackException;

    JournalPosition getJournalPosition() throws FunnelbackException;

    void storeJournalPosition(JournalPosition position) throws FunnelbackException;

}
