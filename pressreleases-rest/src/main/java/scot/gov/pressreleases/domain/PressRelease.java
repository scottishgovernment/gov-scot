package scot.gov.pressreleases.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PressRelease {

    private String id;
    private String title;
    private String summary;
    private String body;
    private String notesToEditors;
    private ZonedDateTime dateTime;
    private String url;
    private String seoName;
    private String client;
    private List<Contact> contacts = new ArrayList<>();
    private List<String> policies = new ArrayList<>();
    private Map<String, String> topics = new HashMap<>();
    private List<Media> mediaAttachments = new ArrayList<>();
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
    public String toString() {
        return "PressRelease{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", body='" + body + '\'' +
                ", notesToEditors='" + notesToEditors + '\'' +
                ", dateTime=" + dateTime +
                ", url='" + url + '\'' +
                ", seoName='" + seoName + '\'' +
                ", client='" + client + '\'' +
                ", contacts=" + contacts +
                ", policies=" + policies +
                ", topics=" + topics +
                ", mediaAttachments=" + mediaAttachments +
                ", updatedDate=" + updatedDate +
                ", publicationType='" + publicationType + '\'' +
                '}';
    }
}
