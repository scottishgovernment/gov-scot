package scot.gov.www.pressreleases.prgloo.rest;

import java.util.List;

/**
 * Created by z441571 on 09/12/2016.
 *
 * Grouped tags returned to identify those assigned to Policies
 */
public class TagGroup {

    private String name;
    private List<Classification> tags;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<Classification> getTags() { return tags; }

    public void setTags(List<Classification> tags) { this.tags = tags; }
}
