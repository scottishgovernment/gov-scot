package scot.gov.publications.repo;

/**
 * Search/paging parameters for {@link PublicationRepository#list(PublicationListQuery)} - bundled
 * into one object rather than passed as individual parameters, since the method otherwise needs
 * more than Sonar's default maximum of 8.
 */
public class PublicationListQuery {

    private int page;
    private int size;
    private String title;
    private String isbn;
    private String filename;
    private String username;
    private String state;
    private String sort;
    private String dir;

    public static PublicationListQuery of(
            int page, int size, String title, String isbn, String filename, String username,
            String state, String sort, String dir) {
        PublicationListQuery query = new PublicationListQuery();
        query.page = page;
        query.size = size;
        query.title = title;
        query.isbn = isbn;
        query.filename = filename;
        query.username = username;
        query.state = state;
        query.sort = sort;
        query.dir = dir;
        return query;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getFilename() {
        return filename;
    }

    public String getUsername() {
        return username;
    }

    public String getState() {
        return state;
    }

    public String getSort() {
        return sort;
    }

    public String getDir() {
        return dir;
    }
}
