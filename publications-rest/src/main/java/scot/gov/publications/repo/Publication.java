package scot.gov.publications.repo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;

public class Publication {

    private String id;

    private String username;

    private String filename;

    private String title;

    private String isbn;

    private Timestamp embargodate;

    private String state;

    private String statedetails;

    private String checksum;

    private Timestamp createddate;

    private Timestamp lastmodifieddate;

    private String contact;

    private String url;

    private int documentcount;

    private int pagecount;

    private boolean embargoed;

    private boolean scheduled;

    private boolean published;

    private String stacktrace;

    @JsonIgnore
    private int fullcount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Timestamp getEmbargodate() {
        return embargodate;
    }

    public void setEmbargodate(Timestamp embargodate) {
        this.embargodate = embargodate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatedetails() {
        return statedetails;
    }

    public void setStatedetails(String statedetails) {
        this.statedetails = statedetails;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Timestamp getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Timestamp createddate) {
        this.createddate = createddate;
    }

    public Timestamp getLastmodifieddate() {
        return lastmodifieddate;
    }

    public void setLastmodifieddate(Timestamp lastmodifieddate) {
        this.lastmodifieddate = lastmodifieddate;
    }

    public int getFullcount() {
        return fullcount;
    }

    public void setFullcount(int fullcount) {
        this.fullcount = fullcount;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDocumentcount() {
        return documentcount;
    }

    public void setDocumentcount(int documentcount) {
        this.documentcount = documentcount;
    }

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public boolean isEmbargoed() {
        return embargoed;
    }

    public void setEmbargoed(boolean embargoed) {
        this.embargoed = embargoed;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }
}
