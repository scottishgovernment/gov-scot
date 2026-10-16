package scot.gov.publications.repo;

/**
 * Page number and page size for a paged repository query - kept separate from the search/sort
 * criteria in {@link PublicationListQuery} since paging is a distinct, reusable concern.
 */
public class Pageable {

    private final int page;

    private final int size;

    public Pageable(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
