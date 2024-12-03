package scot.gov.www.pressreleases.prgloo.rest;

import java.util.List;

/**
 * Wrapper class to handle the format of the data being returned from PRgloo
 */
public class ChangeHistory {

    private List<Change> history;

    public List<Change> getHistory() {
        return history;
    }

    public void setHistory(List<Change> items) {
        this.history = items;
    }

}
