package scot.gov.www.pressreleases.prgloo.rest;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ChangeType {

    // The order here must be preserved to match numeric values in the API.
    UNPUBLISHED(0),
    PUBLISHED(1),
    UPDATED(2);

    private final int value;

    ChangeType(int value) {
        this.value = value;
    }

    @JsonCreator
    public static ChangeType fromValue(int value) {
        for (ChangeType type : ChangeType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}
