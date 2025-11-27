package scot.gov.www.importer.vuelio.rest;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Format from Vuelio API
 *
 * {
 *         "id": "2ba97a6c-b9b9-12a7-8a6f-3aa956475b0d",
 *         "republishCount": 0,
 *         "headLine": "Supporting people at risk of going missing",
 *         "headlineNews": true,
 *         "globalCategory": "Law and Order",
 *         "team": null,
 *         "category": [
 *             "Law and Order"
 *         ],
 *         "region": [],
 *         "businessUnits": [],
 *         "displayDate": "2025-10-03T10:51:32",
 *         "leadParagraph": null,
 *         "coreCopy": "<p>copy content here</p>",
 *         "notesToEditor": "​",
 *         "boilerPlate": "​",
 *         "assets": [],
 *         "published": true,
 *         "dateCreated": "2025-10-03T10:51:32",
 *         "dateModified": "2025-10-03T10:51:32",
 *         "metadata": [
 *             {
 *                 "id": "ea6fa082-101b-b097-0bc9-7daeb527cb14",
 *                 "name": "Distribution Type",
 *                 "values": [
 *                     "News release"
 *                 ],
 *                 "dateCreated": "2025-10-03T10:51:32",
 *                 "dateModified": "2025-10-03T10:51:32"
 *             },
 *             {
 *                 "id": "feba7415-afe5-af80-72ff-49daff13ca5c",
 *                 "name": "headlineNews",
 *                 "values": [
 *                     "True"
 *                 ],
 *                 "dateCreated": "2025-10-03T10:51:32",
 *                 "dateModified": "2025-10-03T10:51:32"
 *             }
 *         ],
 *         "isDeleted": false
 *     }
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentItem {

    private String id;
    private int republishCount;
    private String headLine;
    private boolean headlineNews;
    private String subHeading;
    private String globalCategory;
    private List<String> category = new ArrayList<>();
    private List<String> region = new ArrayList<>();
    private List<String> businessUnits = new ArrayList<>();
    private LocalDateTime displayDate;
    private String leadParagraph;
    private String coreCopy;
    private String notesToEditor;
    private String boilerPlate;
    private List<Asset> assets = new ArrayList<>();
    private boolean published;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
    private List<Metadata> metadata = new ArrayList<>();
    private boolean isDeleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public boolean isHeadlineNews() {
        return headlineNews;
    }

    public void setHeadlineNews(boolean headlineNews) {
        this.headlineNews = headlineNews;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    public String getGlobalCategory() {
        return globalCategory;
    }

    public void setGlobalCategory(String globalCategory) {
        this.globalCategory = globalCategory;
    }

    public int getRepublishCount() { return republishCount; }

    public void setRepublishCount(int republishCount) { this.republishCount = republishCount; }

    public List<String> getCategory() { return category; }

    public void setCategory(List<String> category) { this.category = category; }

    public List<String> getRegion() {
        return region;
    }

    public void setRegion(List<String> region) {
        this.region = region;
    }

    public List<String> getBusinessUnits() {
        return businessUnits;
    }

    public void setBusinessUnits(List<String> businessUnits) {
        this.businessUnits = businessUnits;
    }

    public LocalDateTime getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(LocalDateTime displayDate) {
        this.displayDate = displayDate;
    }

    public String getLeadParagraph() {
        return leadParagraph;
    }

    public void setLeadParagraph(String leadParagraph) {
        this.leadParagraph = leadParagraph;
    }

    public String getCoreCopy() {
        return coreCopy;
    }

    public void setCoreCopy(String coreCopy) {
        this.coreCopy = coreCopy;
    }

    public String getNotesToEditor() {
        return notesToEditor;
    }

    public void setNotesToEditor(String notesToEditor) {
        this.notesToEditor = notesToEditor;
    }

    public String getBoilerPlate() {
        return boilerPlate;
    }

    public void setBoilerPlate(String boilerPlate) {
        this.boilerPlate = boilerPlate;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public List<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }

    public boolean isDeleted() { return isDeleted; }

    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public List<String> getPolicyTags() {
        List<Metadata> policies = metadata.stream().filter(m -> m.getName().contains("Policy")).toList();
        if (policies.isEmpty()) {
            return new ArrayList<>();
        }
        return policies.get(0).getValues();
    }

    public List<String> getTopicTags() {
        List<Metadata> topics = metadata.stream().filter(m -> m.getName().contains("Topic")).toList();
        if (topics.isEmpty()) {
            return new ArrayList<>();
        }
        return topics.get(0).getValues();
    }


    public boolean updatedSinceLastRun(Instant lastRun) {
        return lastRun.isBefore(dateModified.toInstant(ZoneOffset.UTC));
    }

    public boolean isNews() {
        return metadata.stream().anyMatch(m ->
                m.getValues().stream().anyMatch(v ->
                v.contains(Value.NEWS_RELEASE.getDescription())));
    }

    public boolean isStagingNews() {
        return metadata.stream().anyMatch(m ->
                m.getValues().stream().anyMatch(v ->
                        v.contains(Value.NEWS_RELASE.getDescription())));
    }

    public boolean isSpeech() {
        return metadata.stream().anyMatch(m ->
                m.getValues().stream().anyMatch(v ->
                        v.contains(Value.SPEECH.getDescription())));
    }

    public boolean isCorrespondence() {
        return metadata.stream().anyMatch(m ->
                m.getValues().stream().anyMatch(v ->
                        v.contains(Value.CORRESPONDENCE.getDescription())));
    }

    public boolean isWebPublishContent() {
        if (isNews() || isSpeech() || isCorrespondence() || isStagingNews()) {
            return true;
        }
        return false;
    }

    public

    @Override
    String toString() {
        return "ContentItem{" +
                "id='" + id + '\'' +
                ", republishCount=" + republishCount +
                ", headline='" + headLine + '\'' +
                ", headlineNews=" + headlineNews +
                ", globalCategory='" + globalCategory + '\'' +
                ", category=" + category +
                ", region=" + region +
                ", businessUnits=" + businessUnits +
                ", displayDate=" + displayDate +
                ", leadParagraph='" + leadParagraph + '\'' +
                ", coreCopy='" + coreCopy + '\'' +
                ", notesToEditor='" + notesToEditor + '\'' +
                ", boilerPlate='" + boilerPlate + '\'' +
                ", assets=" + assets +
                ", published=" + published +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", metadata=" + metadata +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
