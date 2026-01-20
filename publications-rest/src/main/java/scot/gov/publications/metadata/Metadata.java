package scot.gov.publications.metadata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {

    String id;

    String wpid;

    String title;

    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime publicationDate;

    ZonedDateTime publicationDateWithTimezone;

    PublicationTypeMapper typeMapper = new PublicationTypeMapper();

    String url;

    String alternateUrl;

    String executiveSummary;

    String description;

    String isbn;

    String topic;

    List<String> topics = new ArrayList<>();

    List<String> policies = new ArrayList<>();

    String publicationType;

    String keywords;

    List<String> tags = new ArrayList<>();

    String researchCategory;

    String statisticsCategory;

    EqualityInfo equalityInfo;

    Contact contact;

    String primaryResponsibleDirectorate;

    List<String> secondaryResponsibleDirectorates = new ArrayList<>();

    String primaryResponsibleRole;

    List<String> secondaryResponsibleRoles = new ArrayList<>();

    boolean sensitive = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWpid() {
        return wpid;
    }

    public void setWpid(String wpid) {
        this.wpid = wpid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public ZonedDateTime getPublicationDateWithTimezone() {
        return publicationDateWithTimezone;
    }

    public void setPublicationDateWithTimezone(ZonedDateTime publicationDateWithTimezone) {
        this.publicationDateWithTimezone = publicationDateWithTimezone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlternateUrl() {
        return alternateUrl;
    }

    public void setAlternateUrl(String alternateUrl) {
        this.alternateUrl = alternateUrl;
    }

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public void setExecutiveSummary(String executiveSummary) {
        this.executiveSummary = executiveSummary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getPolicies() {
        return policies;
    }

    public void setPolicies(List<String> policies) {
        this.policies = policies;
    }

    public String getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(String publicationType) {
        this.publicationType = publicationType;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(String researchCategory) {
        this.researchCategory = researchCategory;
    }

    public String getStatisticsCategory() {
        return statisticsCategory;
    }

    public void setStatisticsCategory(String statisticsCategory) {
        this.statisticsCategory = statisticsCategory;
    }

    public EqualityInfo getEqualityInfo() {
        return equalityInfo;
    }

    public void setEqualityInfo(EqualityInfo equalityInfo) {
        this.equalityInfo = equalityInfo;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getPrimaryResponsibleDirectorate() {
        return primaryResponsibleDirectorate;
    }

    public void setPrimaryResponsibleDirectorate(String primaryResponsibleDirectorate) {
        this.primaryResponsibleDirectorate = primaryResponsibleDirectorate;
    }

    public List<String> getSecondaryResponsibleDirectorates() {
        return secondaryResponsibleDirectorates;
    }

    public void setSecondaryResponsibleDirectorates(List<String> secondaryResponsibleDirectorates) {
        this.secondaryResponsibleDirectorates = secondaryResponsibleDirectorates;
    }

    public String getPrimaryResponsibleRole() {
        return primaryResponsibleRole;
    }

    public void setPrimaryResponsibleRole(String primaryResponsibleRole) {
        this.primaryResponsibleRole = primaryResponsibleRole;
    }

    public List<String> getSecondaryResponsibleRoles() {
        return secondaryResponsibleRoles;
    }

    public void setSecondaryResponsibleRoles(List<String> secondaryResponsibleRoles) {
        this.secondaryResponsibleRoles = secondaryResponsibleRoles;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public String normalisedIsbn() {
        return StringUtils.isEmpty(isbn)
                ? ""
                : isbn.replaceAll("\\s", "").replaceAll("-", "");
    }

    public String mappedPublicationType() {
        return typeMapper.map(publicationType);
    }

    public boolean shouldEmbargo() {

        // we never need to embargo a publications whose publication date is in the past
        if (publicationDate.isBefore(LocalDateTime.now())) {
            return false;
        }

        // if the sensitive flag is set or if this is an embargo type then we should embargo it.
        return isSensitive() || typeMapper.isEmbargoType(publicationType);
    }
}
