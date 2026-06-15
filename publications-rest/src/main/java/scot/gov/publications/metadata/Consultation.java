package scot.gov.publications.metadata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Consultation {

    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime openingDate;

    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime closingDate;

    String responseUrl;

    List<ConsultationResponseMethod> alternativeWaysToRespond = new ArrayList<>();

    String consultationUrl;

    public LocalDateTime getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDateTime openingDate) {
        this.openingDate = openingDate;
    }

    public LocalDateTime getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDateTime closingDate) {
        this.closingDate = closingDate;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

    public List<ConsultationResponseMethod> getAlternativeWaysToRespond() {
        return alternativeWaysToRespond;
    }

    public void setAlternativeWaysToRespond(List<ConsultationResponseMethod> alternativeWaysToRespond) {
        this.alternativeWaysToRespond = alternativeWaysToRespond;
    }

    public String getConsultationUrl() {
        return consultationUrl;
    }

    public void setConsultationUrl(String consultationUrl) {
        this.consultationUrl = consultationUrl;
    }
}
