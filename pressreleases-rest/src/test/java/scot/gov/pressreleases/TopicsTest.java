package scot.gov.pressreleases;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Assert;
import org.junit.Test;
import org.onehippo.forge.content.pojo.model.ContentNode;
import scot.gov.pressreleases.domain.PressRelease;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static scot.gov.pressreleases.PressReleaseImporter.GOVSCOT_TITLE;

public class TopicsTest {

    Topics sut = new Topics();

    @Test
    public void addsExpectedTopics() throws RepositoryException {

        // ARRANGE
        ContentNode contentNode = new ContentNode();
        PressRelease pressRelease = new PressRelease();
        Map<String, String> topicMap = new HashMap<>();
        topicMap.put("topic-1-id", "BBB Topic one");
        topicMap.put("topic-2-id", "AAA Topic two");
        topicMap.put("issue-1-id", "BBB Issue two match by title");
        topicMap.put("issue-2-id", "AAA Issue two match by newsTag");
        pressRelease.setTopics(topicMap);
        Session session = sessionWIthTopicsAndIssues();

        // ACT
        Collection<String> actual = sut.getTopics(pressRelease, contentNode, session);

        // ARRANGE
        Collection<String> expected = Arrays.asList("issue-2-id", "topic-2-id", "issue-1-id", "topic-1-id");
        Assert.assertArrayEquals(expected.toArray(), actual.toArray());
    }

    Session sessionWIthTopicsAndIssues() throws RepositoryException {
        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        QueryManager queryManager = mock(QueryManager.class);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        addQuery(queryManager, "govscot:Issue", issuesInRepo());
        addQuery(queryManager, "govscot:Topic", topicsInRepo());
        return session;
    }

    void addQuery(QueryManager queryManager, String type, NodeIterator it) throws RepositoryException {
        String xpath = String.format("//element(*, %s)[hippostd:state = 'published']", type);
        Query query = mock(Query.class);
        QueryResult queryResult = mock(QueryResult.class);
        when(queryManager.createQuery(eq(xpath), any())).thenReturn(query);
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenReturn(it);
    }

    NodeIterator topicsInRepo() throws RepositoryException {
        List<Node> topics = new ArrayList<>();
        topics.add(topicNode("topic-1-id", "BBB Topic one"));
        topics.add(topicNode("topic-2-id", "AAA Topic two"));
        topics.add(topicNode("another-topic-id", "Another topic"));
        return iterator(topics);
    }

    NodeIterator issuesInRepo() throws RepositoryException {
        List<Node> issues = new ArrayList<>();
        issues.add(issueNode("issue-1-id", "BBB Issue two match by title"));
        issues.add(issueNode("issue-2-id", "AAA Issue two match by newsTag...", "issue-2-id"));
        return iterator(issues);
    }

    Node topicNode(String id, String title) throws RepositoryException {
        Node node = mock(Node.class);
        Node handle = mock(Node.class);
        when(handle.getIdentifier()).thenReturn(id);
        when(node.getParent()).thenReturn(handle);
        Property titleProp = mock(Property.class);
        when(titleProp.getString()).thenReturn(title);
        when(node.getProperty(GOVSCOT_TITLE)).thenReturn(titleProp);
        return node;
    }

    Node issueNode(String id, String title, String ... newsIds) throws RepositoryException {
        Node node = topicNode(id, title);
        when(node.hasProperty("govscot:newsTags")).thenReturn(newsIds.length != 0);
        Property property = mock(Property.class);
        List<Value> valueList = new ArrayList<>();
        for (String newsId : newsIds) {
            valueList.add(value(newsId));
        }
        when(property.getValues()).thenReturn(valueList.toArray(new Value[newsIds.length]));
        when(node.getProperty("govscot:newsTags")).thenReturn(property);
        return node;
    }

    Value value(String val) throws RepositoryException {
        Value value = mock(Value.class);
        when(value.getString()).thenReturn(val);
        return value;
    }

    public static NodeIterator iterator(Collection<Node> nodes) {

        Iterator<Node> it = nodes.iterator();

        return new NodeIterator() {

            public boolean hasNext() {
                return  it.hasNext();
            }

            public Node nextNode() {
                return (Node) it.next();
            }

            public void skip(long skipNum) {
                throw new NotImplementedException();
            }

            public long getSize() {
                return nodes.size();
            }

            public long getPosition() {
                throw new NotImplementedException();
            }

            public NodeIterator next() {
                throw new NotImplementedException();
            }
        };
    };
}
