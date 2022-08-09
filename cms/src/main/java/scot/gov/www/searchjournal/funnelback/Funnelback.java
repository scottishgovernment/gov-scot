package scot.gov.www.searchjournal.funnelback;

import java.util.Calendar;

public interface Funnelback {

    void close();

    void publish(String collection, String key, String html) throws FunnelbackException;

    void depublish(String collection, String key) throws FunnelbackException;

    Calendar getJournalPosition() throws FunnelbackException;

    void storeJournalPosition(Calendar position) throws FunnelbackException;

}
