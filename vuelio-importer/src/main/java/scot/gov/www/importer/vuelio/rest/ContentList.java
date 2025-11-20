package scot.gov.www.importer.vuelio.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class to handle the format of the data being returned from Vuelio
 */
public class ContentList {

    private List<ContentItem> items = new ArrayList<>();

    public List<ContentItem> getItems() { return items; }

    public void setItems(List<ContentItem> items) { this.items = items; }
}
