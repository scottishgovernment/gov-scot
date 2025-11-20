package scot.gov.www.importer.vuelio.rest;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * {
 *     "id": "6ed7a023-86c6-a159-4e74-ce5763ddebdb",
 *     "name": "Distribution Type",
 *     "values": [
 *          "News relase"
 *     ],
 *     "dateCreated": "2025-11-21T10:03:09",
 *     "dateModified": "2025-11-21T10:03:09"
 * }
 */

public class Metadata {

    private String id;
    private String name;
    private List<String> values;
    private OffsetDateTime dateCreated;
    private OffsetDateTime dateModified;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<String> getValues() { return values; }

    public void setValues(List<String> values) { this.values = values; }

    public OffsetDateTime getDateCreated() { return dateCreated; }

    public void setDateCreated(OffsetDateTime dateCreated) { this.dateCreated = dateCreated; }

    public OffsetDateTime getDateModified() { return dateModified; }

    public void setDateModified(OffsetDateTime dateModified) { this.dateModified = dateModified; }

}