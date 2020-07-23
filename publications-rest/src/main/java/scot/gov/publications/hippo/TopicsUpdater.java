package scot.gov.publications.hippo;

import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.metadata.Metadata;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static scot.gov.publications.hippo.XpathQueryHelper.topicHandleQuery;

/**
 * Object capable of updating the topics a node has based on Metadara object.
 *
 * This owrks by adding any topics contained in the metedata to any already existing.
 *
 * The initial version of the metadata was backed by the business solutions api had a single topic.  This is
 * contained in the metadata.topic field.  This feidl contains the name of a topic as it apears on the legacy
 * publications registration form.   To complicate matters these topics have changed over time and to we have
 * to look it up in the "topics" map.  Then we look up its path.
 *
 * The metadata.topics field contains ids as they appear in the metadata rest api.
 *
 */
public class TopicsUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(TopicsUpdater.class);

    private static final String RESEARCH = "Research";

    private Map<String, String> topics = new HashMap<>();

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    public TopicsUpdater(Session session) {
        this.session = session;

        // Mappings from legacy topic to the new topics.
        topics.put("Agriculture", "farming-and-rural");
        topics.put("Arts and Culture", "arts-culture-and-sport");
        topics.put("Business and Industry", "business-industry-and-innovation");
        topics.put("Economy", "economy");
        topics.put("Education and Training", "education");
        topics.put("Environment", "environment-and-climate-change");
        topics.put("Energy", "energy");
        topics.put("Health and Community Care", "health-and-social-care");
        topics.put("Housing", "housing");
        topics.put("International", "international");
        topics.put("Law, Order and Public Safety", "law-and-order");
        topics.put("Marine and Fisheries", "marine-and-fisheries");
        topics.put("People and Society", "communities-and-third-sector");
        topics.put("Planning", RESEARCH.toLowerCase());
        topics.put("Public Sector", "public-sector");
        topics.put("Regeneration", "building-planning-and-design");
        topics.put(RESEARCH, RESEARCH.toLowerCase());
        topics.put("Statistics", "statistics");
        topics.put("Sustainable Development", "environment-and-climate-change");
    }

    /**
     Update a publication node such that any topics contained in the metedata re added to the node.
     *
     * @param publicationNode The publication publicationNode to add the topics to.
     *
     * @param metadata
     * @throws RepositoryException
     */
    public void ensureTopics(ContentNode publicationNode, Metadata metadata) throws RepositoryException {
        ensureLegacyTopic(publicationNode, metadata.getTopic());
        for (String topic : metadata.getTopics()) {
            ensureTopic(publicationNode, topic);
        }
    }

    private void ensureLegacyTopic(ContentNode publicationNode, String topicName) throws RepositoryException {
        String mappedTopic = topics.get(topicName);
        if (mappedTopic == null) {
            LOG.info("No such legacy topic {}", topicName);
            return;
        }
        ensureTopic(publicationNode, mappedTopic);
    }

    private void ensureTopic(ContentNode publicationNode, String topic) throws RepositoryException {
        String xpath = topicHandleQuery(topic);
        Node topicNode = hippoUtils.findOneXPath(session, xpath);
        if (topicNode == null) {
            LOG.warn("No such topic {}", topic);
            return;
        }
        ensureTopic(publicationNode, topicNode);
    }

    private void ensureTopic(ContentNode publicationNode, Node topicNode) throws RepositoryException {
        String id = topicNode.getIdentifier();
        Optional<ContentNode> existingTopicNode = publicationNode.getNodes().stream()
                .filter(cn -> "govscot:topics".equals(cn.getName()))
                .filter(t -> id.equals(t.getProperty("hippo:docbase").getValue()))
                .findFirst();
        if (existingTopicNode.isPresent()) {
            // the publication already has this topic
            return;
        }

        ContentNode newTopicNode = new ContentNode();
        newTopicNode.setName("govscot:topics");
        newTopicNode.setPrimaryType("hippo:mirror");
        newTopicNode.setProperty("hippo:docbase", topicNode.getIdentifier());
        publicationNode.addNode(newTopicNode);
    }

}
