package scot.gov.www.pressreleases.prgloo.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.startsWithAny;

/**
 * Wrapper class to handle the format of the data being returned from PRgloo
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagGroups {

    @JsonProperty(value = "tagGroups")
    private List<TagGroup> tagGroupList;

    public List<TagGroup> getTagGroupList() {
        return tagGroupList;
    }

    public void setTagGroupList(List<TagGroup> tagGroupList) {
        this.tagGroupList = tagGroupList;
    }

    public TagGroup getPolicyTagGroup() {
        return getTagsWithPrefix("policy");
    }

    public TagGroup getTopicsTagGroup() {
        // PRGloo changed the category on us, this ensures both old and new are recognised
        return getTagsWithPrefix("topics", "website categories");
    }

    TagGroup getTagsWithPrefix(String ... prefixes) {
        return tagGroupList.stream()
                .filter(tagGroup -> tagGroupMatches(tagGroup, prefixes))
                .findFirst().orElse(null);
    }

    boolean tagGroupMatches(TagGroup tagGroup, String ... prefixes) {
        String name = tagGroup.getName().toLowerCase();
        return startsWithAny(name, prefixes);
    }
}

