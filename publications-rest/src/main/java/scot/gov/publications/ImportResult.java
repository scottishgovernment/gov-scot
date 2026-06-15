package scot.gov.publications;

/**
 * Summary of a successful APS zip import, returned by {@link ApsZipImporter#importApsZip} for the
 * caller to persist against the {@link scot.gov.publications.repo.Publication} job record.
 */
public class ImportResult {

    private final String path;

    private final int documentCount;

    private final int pageCount;

    private final boolean embargoed;

    private final boolean scheduled;

    private final boolean published;

    public ImportResult(
            String path,
            int documentCount,
            int pageCount,
            boolean embargoed,
            boolean scheduled,
            boolean published) {
        this.path = path;
        this.documentCount = documentCount;
        this.pageCount = pageCount;
        this.embargoed = embargoed;
        this.scheduled = scheduled;
        this.published = published;
    }

    public String getPath() {
        return path;
    }

    public int getDocumentCount() {
        return documentCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public boolean isEmbargoed() {
        return embargoed;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public boolean isPublished() {
        return published;
    }
}
