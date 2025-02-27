package scot.gov.www.pressreleases.prgloo.rest;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created by z441571 on 18/10/2016.
 *
 * Format from PRgloo API documentation
 * {
 *     "attachments":[ Attachment ],
 *     "body": String,
 *     "contentStreamId": String,
 *     "created": UTC DateTime,
 *     "embeddedResources":[ Attachment ],
 *     "event":{
 *         "endDateTime": UTC DateTime,
 *         "location": String,
 *         "startDateTime": UTC DateTime
 *     },
 *     "heroImage": Attachment,
 *     "id": String,
 *     "isFeatured": Boolean,
 *     "keywords": String,
 *     "publishedFrom": UTC DateTime,
 *     "regions":[ Classification ],
 *     "sender": Sender ,
 *     "seoName": String,
 *     "summary": String,
 *     "tags":[ Classification ],
 *     "tagGroups": [
 *          "name": String
 *          "tags": [ Classification]
 *     ]
 *     "title": String,
 *     "updated": UTC DateTime
 * }
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PressReleaseItem {

    private String id;
    private String contentStreamId;
    private String title;
    private String summary;
    private String body;
    private OffsetDateTime created;
    private OffsetDateTime updated;
    private OffsetDateTime publishedFrom;
    private Sender sender;
    private String seoName;
    private List<TagGroup> tagGroups;
    private List<Classification> regions;
    private List<Attachment> attachments;
    private List<Attachment> embeddedResources;
    private Attachment heroImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentStreamId() {
        return contentStreamId;
    }

    public void setContentStreamId(String contentStreamId) {
        this.contentStreamId = contentStreamId;
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

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public OffsetDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(OffsetDateTime updated) {
        this.updated = updated;
    }

    public OffsetDateTime getPublishedFrom() {
        return publishedFrom;
    }

    public void setPublishedFrom(OffsetDateTime publishedFrom) {
        this.publishedFrom = publishedFrom;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getSeoName() {
        return seoName;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }

    public List<TagGroup> getTagGroups() { return tagGroups; }

    public void setTagGroups(List<TagGroup> tagGroups) { this.tagGroups = tagGroups; }

    public List<Classification> getRegions() {
        return regions;
    }

    public void setRegions(List<Classification> regions) {
        this.regions = regions;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Attachment> getEmbeddedResources() {
        return embeddedResources;
    }

    public void setEmbeddedResources(List<Attachment> embeddedResources) {
        this.embeddedResources = embeddedResources;
    }

    public Attachment getHeroImage() { return heroImage; }

    public void setHeroImage(Attachment heroImage) {
        this.heroImage = heroImage;
    }

    @Override
    public String toString() {
        return "PressReleaseItem{" +
                "id='" + id + '\'' +
                ", contentStreamId='" + contentStreamId + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", body='" + body + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", publishedFrom=" + publishedFrom +
                ", sender=" + sender +
                ", seoName='" + seoName + '\'' +
                ", tagGroups=" + tagGroups +
                ", regions=" + regions +
                ", attachments=" + attachments +
                ", embeddedResources=" + embeddedResources +
                ", heroImage=" + heroImage +
                '}';
    }
}
