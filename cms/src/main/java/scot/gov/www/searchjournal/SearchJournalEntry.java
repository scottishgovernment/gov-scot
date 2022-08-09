package scot.gov.www.searchjournal;

import java.util.Calendar;

public class SearchJournalEntry {

    private String url;

    private Calendar timestamp;

    private String collection;

    private String action;

    private long attempt = 0;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getAttempt() {
        return attempt;
    }

    public void setAttempt(long attempt) {
        this.attempt = attempt;
    }

    public static SearchJournalEntry publishEntry(String url, String collection, Calendar timestamp) {
        SearchJournalEntry entry = new SearchJournalEntry();
        entry.setAction("publish");
        entry.setAttempt(0);
        entry.setTimestamp(timestamp);
        entry.setUrl(url);
        entry.setCollection(collection);
        return entry;
    }
}