package scot.gov.www.importer.vuelio.rest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * {
 *     "id": "6ed7a023-86c6-a159-4e74-ce5763ddebdb",
 *     "name": "Distribution Type",
 *     "values": [
 *          "News release"
 *     ],
 *     "dateCreated": "2025-11-21T10:03:09",
 *     "dateModified": "2025-11-21T10:03:09"
 * }
 */

public class Metadata {

    private String id;
    private String name;
    private List<String> values = new ArrayList<>();
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<String> getValues() { return values; }

    public void setValues(List<String> values) { this.values = values; }

    public LocalDateTime getDateCreated() { return dateCreated; }

    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }

    public LocalDateTime getDateModified() { return dateModified; }

    public void setDateModified(LocalDateTime dateModified) { this.dateModified = dateModified; }

    @Override
    public String toString() {
        return "Metadata{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", values=" + values +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                '}';
    }
}