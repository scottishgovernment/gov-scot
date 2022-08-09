package scot.gov.searchjournal;

import java.util.Calendar;

public class JournalPositionSource {

    private static Calendar lastJournalPosition;

    public static void setLastJournalPosition(Calendar lastJournalPosition) {
        JournalPositionSource.lastJournalPosition = lastJournalPosition;
    }

    public static Calendar getLastJournalPosition() {
        return lastJournalPosition;
    }
}
