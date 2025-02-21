package scot.gov.www.pressreleases.prgloo.rest;

/**
 * Created by z441571 on 19/10/2016.
 *
 * Format from PRgloo API documentation
 * {
 *    "id": String,
 *    "name": String
 * }
 *
 */
public class Classification {

    private String id;
    private String name;
    private String seoName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeoName() { return seoName; }

    public void setSeoName(String seoName) { this.seoName = seoName; }
}
