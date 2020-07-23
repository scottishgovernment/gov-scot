package scot.gov.publications.repo;

import java.util.Calendar;

public class Publication {

    private String id;

    private String username;

    private String filename;

    private String title;

    private String isbn;

    private Calendar embargodate;

    private String state;

    private String statedetails;

    private Calendar createddate;

    private Calendar lastmodifieddate;

    private String contact;

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

    public Calendar getEmbargodate() {
        return embargodate;
    }

    public void setEmbargodate(Calendar embargodate) {
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

    public Calendar getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Calendar createddate) {
        this.createddate = createddate;
    }

    public Calendar getLastmodifieddate() {
        return lastmodifieddate;
    }

    public void setLastmodifieddate(Calendar lastmodifieddate) {
        this.lastmodifieddate = lastmodifieddate;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
