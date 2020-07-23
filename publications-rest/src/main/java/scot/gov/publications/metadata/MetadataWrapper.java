package scot.gov.publications.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The metadata file contained in the zip is a json fole with a single metadata property so we need  wrapper object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataWrapper {

    private Metadata metadata;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
