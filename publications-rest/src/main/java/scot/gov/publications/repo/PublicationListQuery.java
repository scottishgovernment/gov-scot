package scot.gov.publications.repo;

/**
 * Search and sort criteria for {@link PublicationRepository#list(Pageable, PublicationListQuery)} -
 * paging itself is a separate concern, see {@link Pageable}. Built via {@link #create()} plus the
 * fluent setters below rather than one large constructor, so no single method needs more than
 * Sonar's default maximum of 8 parameters.
 */
public class PublicationListQuery {

    private String title = "";
    private String isbn = "";
    private String filename = "";
    private String username = "";
    private String state = "";
    private String sort = "";
    private String dir = "";

    public static PublicationListQuery create() {
        return new PublicationListQuery();
    }

    public PublicationListQuery title(String title) {
        this.title = title;
        return this;
    }

    public PublicationListQuery isbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public PublicationListQuery filename(String filename) {
        this.filename = filename;
        return this;
    }

    public PublicationListQuery username(String username) {
        this.username = username;
        return this;
    }

    public PublicationListQuery state(String state) {
        this.state = state;
        return this;
    }

    public PublicationListQuery sort(String sort) {
        this.sort = sort;
        return this;
    }

    public PublicationListQuery dir(String dir) {
        this.dir = dir;
        return this;
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
