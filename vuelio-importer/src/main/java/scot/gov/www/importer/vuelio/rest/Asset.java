package scot.gov.www.importer.vuelio.rest;

/**
 * Format from Vuelio API
 * {
 *     "id": "3a72a5a2-1843-8f48-4d3f-ebedba458ed6",
 *     "link": "https://dmsqaapi.vuelio.co.uk/publicitem/922238e2-95db-4364-9f9c-19f8906c6492",
 *     "title": "download",
 *     "description": "download",
 *     "featureImage": true,
 *     "published": true
 * }
 *
 */
public class Asset {
    
    private String id;
    private String link;
    private String title;
    private String description;
    private boolean featuredImage;
    private boolean published;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public boolean isFeaturedImage() { return featuredImage; }

    public void setFeaturedImage(boolean featuredImage) { this.featuredImage = featuredImage; }

    public boolean isPublished() { return published; }

    public void setPublished(boolean published) { this.published = published; }

    public String getAssetType() {
        if (isFeaturedImage()) {
            return "Image";
        }
        return "Document";
    }

}
