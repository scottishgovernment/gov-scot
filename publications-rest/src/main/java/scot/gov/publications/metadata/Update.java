package scot.gov.publications.metadata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Update {

    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime lastUpdated;

    ZonedDateTime lastUpdatedWithTimezone;

    String updateText;

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUpdateText() {
        return updateText;
    }

    public void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    public ZonedDateTime getLastUpdatedWithTimezone() {
        return lastUpdatedWithTimezone;
    }

    public void setLastUpdatedWithTimezone(ZonedDateTime lastUpdatedWithTimezone) {
        this.lastUpdatedWithTimezone = lastUpdatedWithTimezone;
    }
}
