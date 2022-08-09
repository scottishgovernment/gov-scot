package scot.gov.www.searchjournal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RetryPolicy {

    private Map<Long, Long> attemptToBackoffPeriod = new HashMap<>();

    public RetryPolicy() {
        attemptToBackoffPeriod.put(0L, TimeUnit.MINUTES.toMillis(1));
        attemptToBackoffPeriod.put(1L, TimeUnit.MINUTES.toMillis(5));
        attemptToBackoffPeriod.put(2L, TimeUnit.MINUTES.toMillis(10));
        attemptToBackoffPeriod.put(3L, TimeUnit.MINUTES.toMillis(20));
    }

    public boolean shouldRetry(SearchJournalEntry entry) {
        return attemptToBackoffPeriod.containsKey(entry.getAttempt());
    }

    public long getBackoffPeriodInMillis(SearchJournalEntry entry) {
        long attempt = entry.getAttempt();
        if (!attemptToBackoffPeriod.containsKey(attempt)) {
            return -1;
        }

        return attemptToBackoffPeriod.get(attempt);
    }
}
