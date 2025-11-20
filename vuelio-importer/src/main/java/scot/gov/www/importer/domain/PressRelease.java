package scot.gov.www.importer.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.util.List;

public class PressRelease {

    private String id;
    private String title;
    private String summary;
    private String body;
    private ZonedDateTime dateTime;
    private String url;
    private String seoName;
    private List<String> policies;
    private List<String> topics;
    private List<Media> mediaAttachments;
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

    public List<String> getPolicies() {
        return policies;
    }

    public void setPolicies(List<String> policies) {
        this.policies = policies;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
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
                .append(dateTime, that.dateTime)
                .append(url, that.url)
                .append(policies, that.policies)
                .append(topics, that.topics)
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
                .append(dateTime)
                .append(url)
                .append(policies)
                .append(topics)
                .append(mediaAttachments)
                .toHashCode();
    }
}
