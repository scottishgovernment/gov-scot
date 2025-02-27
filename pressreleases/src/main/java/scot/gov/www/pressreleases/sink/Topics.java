package scot.gov.www.pressreleases.sink;

import org.onehippo.forge.content.pojo.model.ContentNode;
import scot.gov.www.pressreleases.domain.PressRelease;

import javax.jcr.*;
import javax.jcr.query.Query;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.hippoecm.repository.api.HippoNodeType.HIPPO_DOCBASE;

public class Topics {

    private static final String GOVSCOT_TOPICS = "govscot:topics";

    private static final String GOVSCOT_TITLE = "govscot:title";

    /**
     * Add topics for this press release.
     *
     * This adds any matching govscot:Topic's based on the name matching the title of the topic node in bloomreach.
     * It then adds any govscot:Issue's whose title matches or fi their id matched one of its govscot:newsTags.
     *
     * The topics added are deduplicated and sorted by name.
     */
    public void addTopics(PressRelease release, ContentNode contentNode, Session session) throws RepositoryException {
        getTopics(release, contentNode, session).stream().forEach(id -> addTopicNode(contentNode, id));
    }

    public Collection<String> getTopics(PressRelease release, ContentNode contentNode, Session session) throws RepositoryException {
        SortedMap<String, String> topicMap = new TreeMap<>();
        addTopicsForTopics(session, release, topicMap);
        addTopicsForIssues(session, release, topicMap);
        return topicMap.values();
    }

    void addTopicsForTopics(Session session, PressRelease release, Map<String, String> topicMap ) throws RepositoryException {
        NodeIterator it = getType(session, "govscot:Topic");
        while (it.hasNext()) {
            Node topic = it.nextNode();
            String title = topic.getProperty(GOVSCOT_TITLE).getString();
            if (releaseContainsTopic(release, title)) {
                topicMap.put(title, topic.getParent().getIdentifier());
            }
        }
    }

    void addTopicsForIssues(Session session, PressRelease release, Map<String, String> topicMap ) throws RepositoryException {
        NodeIterator it = getType(session, "govscot:Issue");
        while (it.hasNext()) {
            Node issue = it.nextNode();
            String title = issue.getProperty(GOVSCOT_TITLE).getString();
            String handleIdentifier = issue.getParent().getIdentifier();
            if (releaseContainsTopic(release, title)) {
                topicMap.put(title, handleIdentifier);
                continue;
            }

            Set<String> newsTags = newsTags(issue);
            for (String topicKey : release.getTopics().keySet()) {
                if (newsTags.contains(topicKey)) {
                    topicMap.put(title, handleIdentifier);
                }
            }
        }
    }

    boolean releaseContainsTopic(PressRelease release, String topic) {
        return release.getTopics().values().stream().filter(s -> equalsIgnoreCase(s, topic)).findFirst().isPresent();
    }

    void addTopicNode(ContentNode contentNode, String identifier) {
        ContentNode topicNode = new ContentNode();
        topicNode.setPrimaryType("hippo:mirror");
        topicNode.setName(GOVSCOT_TOPICS);
        topicNode.setProperty(HIPPO_DOCBASE, identifier);
        contentNode.addNode(topicNode);
    }

    Set<String> newsTags(Node node) throws RepositoryException {
        if (!node.hasProperty("govscot:newsTags")) {
            return Collections.emptySet();
        }

        HashSet<String> set = new HashSet<>();
        Value[] values = node.getProperty("govscot:newsTags").getValues();
        for (Value value : values) {
            set.add(value.getString());
        }
        return set;
    }

    NodeIterator getType(Session session, String type) throws RepositoryException {
        String xpath = String.format("//element(*, %s)[hippostd:state = 'published']", type);
        Query q = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        return q.execute().getNodes();
    }
}
