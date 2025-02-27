package scot.gov.www.pressreleases.prgloo.rest;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

/**
 * Created by z441571 on 18/10/2016.
 *
 * Format from PRgloo API documentation
 * {
 *     "contentStreamId": String,
 *     "docId": String,
 *     "timestamp": UTC DateTime,
 *     "operation": Int
 * }
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Change {

    private String contentStreamId;
    private String docId;
    private OffsetDateTime timestamp;
    private ChangeType operation;

    public String getContentStreamId() {
        return contentStreamId;
    }

    public void setContentStreamId(String contentStreamId) {
        this.contentStreamId = contentStreamId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ChangeType getOperation() {
        return operation;
    }

    public void setOperation(ChangeType operation) {
        this.operation = operation;
    }

}
