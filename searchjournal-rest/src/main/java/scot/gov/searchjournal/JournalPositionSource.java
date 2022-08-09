package scot.gov.searchjournal;

import java.util.Calendar;

public class JournalPositionSource {

    private static JournalPositionSource instance = new JournalPositionSource();

    private Calendar lastJournalPosition;

    private JournalPositionSource() {
        // hide constructor
    }

    public static JournalPositionSource getInstance() {
        return instance;
    }

    public void setLastJournalPosition(Calendar lastJournalPosition) {
        this.lastJournalPosition = lastJournalPosition;
    }

    public Calendar getLastJournalPosition() {
        return lastJournalPosition;
    }
}
