package scot.gov.www.searchjournal.funnelback;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class JournalPosition {

    private Calendar position;

    private long sequence;

    public Calendar getPosition() {
        return position;
    }

    public void setPosition(Calendar position) {
        this.position = position;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String toString() {
        GregorianCalendar cal = (GregorianCalendar) position;
        ZonedDateTime zdt = cal.toZonedDateTime();
        return zdt.toString() + " " + sequence;
    }
}
