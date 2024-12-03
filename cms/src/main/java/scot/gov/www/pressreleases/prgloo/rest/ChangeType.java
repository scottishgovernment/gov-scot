package scot.gov.www.pressreleases.prgloo.rest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChangeType {

    // The order here must be preserved to match numeric values in the API.
    UNPUBLISHED,
    PUBLISHED,
    UPDATED;

    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
