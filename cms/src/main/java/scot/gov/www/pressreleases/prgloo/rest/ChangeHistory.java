package scot.gov.www.pressreleases.prgloo.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class to handle the format of the data being returned from PRgloo
 */
public class ChangeHistory {

    private List<Change> history = new ArrayList<>();

    public List<Change> getHistory() {
        return history;
    }

    public void setHistory(List<Change> items) {
        this.history = items;
    }

}
