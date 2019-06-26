package scot.gov.www.rest.metadata;

import java.util.List;

/**
 * Created by z418868 on 26/06/2019.
 */
public class PolicyMetadataItem extends MetadataItem {

    private List<String> topics;

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
