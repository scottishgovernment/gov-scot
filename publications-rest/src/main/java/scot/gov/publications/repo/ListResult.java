package scot.gov.publications.repo;

import java.util.List;

public class ListResult {

    public static ListResult result(List<Publication> publications, long totalSize, int page, int pageSize) {
        ListResult r = new ListResult();
        r.setPublications(publications);
        r.setTotalSize(totalSize);
        r.setPage(page);
        r.setPageSize(pageSize);
        return r;
    }


    private long totalSize;
    private int page;
    private int pageSize;
    private List<Publication> publications;

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

}
