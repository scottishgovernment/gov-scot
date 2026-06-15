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
}
