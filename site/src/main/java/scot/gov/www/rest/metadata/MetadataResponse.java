package scot.gov.www.rest.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Object returned in response to requests for metedata
 */
public class MetadataResponse {

    List<MetadataItem> data = new ArrayList<>();

    public List<MetadataItem> getData() {
        return data;
    }

    public void setData(List<MetadataItem> data) {
        this.data = data;
    }
}
