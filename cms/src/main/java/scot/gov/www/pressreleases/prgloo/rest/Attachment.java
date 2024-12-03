package scot.gov.www.pressreleases.prgloo.rest;

import java.time.OffsetDateTime;

/**
 * Created by z441571 on 19/10/2016.
 *
 * Format from PRgloo API documentation
 * {
 *     "contentLength": Long,
 *     "contentType": String,
 *     "description": String,
 *     "fileName": String,
 *     "modified": UTC DateTime,
 *     "name": String,
 *     "publicId": String,
 *     "seoName": String
 * }
 *
 */
public class Attachment {
    
    private long contentLength;
    private String contentType;
    private String description;
    private String fileName;
    private OffsetDateTime modified;
    private String name;
    private String publicId;
    private String seoName;

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public OffsetDateTime getModified() {
        return modified;
    }

    public void setModified(OffsetDateTime modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getSeoName() {
        return seoName;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }

}
