package scot.gov.publications.hippo;

import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.forge.content.pojo.model.ContentPropertyType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static scot.gov.publications.hippo.Constants.HIPPOSTD_TAGS;

/**
 * Logic used to update tags.
 */
public class TagUpdater {

    /**
     * Update the tags on the publicationNode.  This will add any tags in the metadata that do not already appear.
     */
    public void updateTags(ContentNode publicationNode, List<String> tags) {
        Set<String> allTags = existingTags(publicationNode);
        allTags.addAll(tags);
        publicationNode.setProperty(HIPPOSTD_TAGS, ContentPropertyType.STRING, allTags.toArray(new String [allTags.size()]));
    }

    private Set<String> existingTags(ContentNode publicationNode) {
        return publicationNode.hasProperty(HIPPOSTD_TAGS)
            ? new HashSet<>(publicationNode.getProperty(HIPPOSTD_TAGS).getValues())
            : new HashSet<>();
    }
}
