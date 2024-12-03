package scot.gov.www.pressreleases.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class PressRelease {

    private String id;
    private String title;
    private String summary;
    private String body;
    private String internalNotes;
    private String notesToEditors;
    private ZonedDateTime dateTime;
    private String url;
    private String seoName;
    private String newsAreaId;
    private String client;
    private List<Contact> contacts;
    private List<String> policies;
    private Map<String, String> topics;
    private String boilerPlateTitle;
    private String boilerPlateDescription;
    private String statusCode;
    private String statusDescription;
    private List<Media> mediaAttachments;
    @JsonProperty("updatedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime updatedDate;
    private String publicationType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public String getNotesToEditors() {
        return notesToEditors;
    }

    public void setNotesToEditors(String notesToEditors) {
        this.notesToEditors = notesToEditors;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSeoName() {
        return seoName;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }

    public void setNewsAreaId(String newsAreaId) {
        this.newsAreaId = newsAreaId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<String> getPolicies() {
        return policies;
    }

    public void setPolicies(List<String> policies) {
        this.policies = policies;
    }

    public Map<String, String> getTopics() {
        return topics;
    }

    public void setTopics(Map<String, String> topics) {
        this.topics = topics;
    }

    public void setBoilerPlateTitle(String boilerPlateTitle) {
        this.boilerPlateTitle = boilerPlateTitle;
    }

    public void setBoilerPlateDescription(String boilerPlateDescription) {
        this.boilerPlateDescription = boilerPlateDescription;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public List<Media> getMediaAttachments() {
        return mediaAttachments;
    }

    public void setMediaAttachments(List<Media> mediaAttachments) {
        this.mediaAttachments = mediaAttachments;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getPublicationType() { return publicationType; }

    public void setPublicationType(String publicationType) { this.publicationType = publicationType;  }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PressRelease)) {
            return false;
        }

        PressRelease that = (PressRelease) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(title, that.title)
                .append(summary, that.summary)
                .append(body, that.body)
                .append(internalNotes, that.internalNotes)
                .append(notesToEditors, that.notesToEditors)
                .append(dateTime, that.dateTime)
                .append(url, that.url)
                .append(newsAreaId, that.newsAreaId)
                .append(client, that.client)
                .append(contacts, that.contacts)
                .append(policies, that.policies)
                .append(topics, that.topics)
                .append(boilerPlateTitle, that.boilerPlateTitle)
                .append(boilerPlateDescription, that.boilerPlateDescription)
                .append(statusCode, that.statusCode)
                .append(statusDescription, that.statusDescription)
                .append(mediaAttachments, that.mediaAttachments)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(title)
                .append(summary)
                .append(body)
                .append(internalNotes)
                .append(notesToEditors)
                .append(dateTime)
                .append(url)
                .append(newsAreaId)
                .append(client)
                .append(contacts)
                .append(policies)
                .append(topics)
                .append(boilerPlateTitle)
                .append(boilerPlateDescription)
                .append(statusCode)
                .append(statusDescription)
                .append(mediaAttachments)
                .toHashCode();
    }
}
