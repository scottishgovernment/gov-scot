package scot.gov.publications.hippo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.*;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static scot.gov.publications.hippo.Constants.HIPPOSTD_STATE;

public class HippoUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HippoUtils.class);

    @FunctionalInterface
    public interface ThrowingPredicate {
        boolean test(Node t) throws RepositoryException;
    }

    @FunctionalInterface
    public interface ThrowingConsumer {
        void accept(Node t) throws RepositoryException;
    }

    public void apply(NodeIterator it, ThrowingConsumer consumer) throws RepositoryException {
        apply(it, n -> true, consumer);
    }

    public boolean hasPublishedVariant(Node node) throws RepositoryException {
        Node variant = getVariant(node);
        return isPublished(variant);
    }

    public boolean isPublished(Node node) throws RepositoryException {
        if (!"published".equals(node.getProperty(HIPPOSTD_STATE).getString())) {
            return false;
        }

        if (!node.hasProperty("hippo:availability")) {
            return false;
        }
        return contains(node.getProperty("hippo:availability").getValues(), "live");
    }

    public boolean contains(Node node, String property, String value) throws RepositoryException {
        if (!node.hasProperty(property)) {
            return false;
        }
        Value [] values = node.getProperty(property).getValues();
        return contains(values, value);
    }
    public boolean contains(Value [] values, String str) throws RepositoryException {
        for (Value value : values) {
            if (str.equals(value.getString())) {
                return true;
            }
        }
        return false;
    }

    public Node getVariant(Node node) throws RepositoryException {
        return getVariant(node.getNodes(node.getName()));
    }

    public Node getVariant(NodeIterator it) throws RepositoryException {
        Map<String, Node> byState = new HashMap<>();
        apply(it,
                this::hasState,
                node -> byState.put(node.getProperty(HIPPOSTD_STATE).getString(), node));
        return firstNonNull(
                byState.get("published"),
                byState.get("unpublished"),
                byState.get("draft"));
    }

    public Node getPublishedVariant(Node node) throws RepositoryException {
        Node variant = getVariant(node);
        return isPublished(variant) ? variant : null;
    }

    boolean hasState(Node node) throws RepositoryException {
        return node.hasProperty(HIPPOSTD_STATE);
    }

    public void apply(NodeIterator it, ThrowingPredicate predicate, ThrowingConsumer consumer) throws RepositoryException {
        while (it.hasNext()) {
            Node node = it.nextNode();
            if (predicate.test(node)) {
                consumer.accept(node);
            }
        }
    }

    public Node find(NodeIterator it, ThrowingPredicate predicate) throws RepositoryException {
        while (it.hasNext()) {
            Node node = it.nextNode();
            if (predicate.test(node)) {
                return node;
            }
        }
        return null;
    }

    public List<String> pathFromNode(Node node) throws RepositoryException {
        List<String> path = new ArrayList<>();
        String pathFromGovscot = substringAfter(node.getPath(), "/content/documents/govscot/");
        path.addAll(asList(pathFromGovscot.split("/")));
        return path;
    }

    public void setPropertyIfAbsent(Node node, String property, String value) throws RepositoryException {
        if (node.hasProperty(property)) {
            return;
        }
        node.setProperty(property, value);
    }

    public Node addHtmlNodeIfAbsent(Node node, String name, String value) throws RepositoryException {
        // if the node already exists then return it
        if (node.hasNode(name)) {
            return node.getNode(name);
        }

        // it does not already exist, create it
        return createHtmlNode(node, name, value);
    }

    public Node ensureHtmlNode(Node node, String name, String value) throws RepositoryException {
        // if the node exists already then delete it
        ensureRemoved(node, name);

        // create the node
        return createHtmlNode(node, name, value);
    }

    private Node createHtmlNode(Node node, String name, String value) throws RepositoryException {
        Node contentNode = node.addNode(name, "hippostd:html");
        contentNode.setProperty("hippostd:content", StringUtils.defaultString(value, ""));
        return contentNode;
    }

    public Node ensureNode(Node parent, String name, String primaryType, String... mixins) throws RepositoryException {
        if (parent.hasNode(name)) {
            return parent.getNode(name);
        }
        return createNode(parent, name, primaryType, mixins);
    }

    public Node createNode(Node parent, String name, String primaryType, String... mixins) throws RepositoryException {
        Node node = parent.addNode(name, primaryType);
        for (String mixin : mixins) {
            node.addMixin(mixin);
        }
        return node;
    }

    public void ensureRemoved(Node node, String name) throws RepositoryException {
        if (node.hasNode(name)) {
            node.getNode(name).remove();
        }
    }

    public void ensurePropertyRemoved(Node node, String name) throws RepositoryException {
        if (node.hasProperty(name)) {
            node.getProperty(name).remove();
        }
    }

    public void ensureMixinRemoved(Node node, String type) throws RepositoryException {
        if(node.isNodeType(type)) {
            node.removeMixin(type);
        }
    }

    public void removeChildren(Node node) throws RepositoryException {
        NodeIterator nodeIt = node.getNodes();
        while (nodeIt.hasNext()) {
            nodeIt.nextNode().remove();
        }
    }

    public void removeSiblings(Node node) throws RepositoryException {
        NodeIterator it = node.getParent().getNodes();
        while (it.hasNext()) {
            Node sibling = it.nextNode();
            if (!sibling.getIdentifier().equals(node.getIdentifier())) {
                sibling.remove();
            }
        }
    }

    public void setPropertyStrings(Node node, String property, Collection<String> values) throws RepositoryException {
        node.setProperty(property, values.toArray(new String[values.size()]), PropertyType.STRING);
    }

    public Node findOne(Session session, String queryTemplate, Object... args) throws RepositoryException {
        return findOneQuery(session, queryTemplate, Query.SQL, args);
    }

    public Node findFirst(Session session, String queryTemplate, Object... args) throws RepositoryException {
        String sql = String.format(queryTemplate, args);
        Query queryObj = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
        QueryResult result = queryObj.execute();
        return result.getNodes().hasNext()
                ? result.getNodes().nextNode()
                : null;
    }

    public Node findOneQuery(Session session, String queryTemplate, String type, Object... args) throws RepositoryException {
        String sql = String.format(queryTemplate, args);
        LOG.info("findOneQuery {}", sql);
        Query queryObj = session.getWorkspace().getQueryManager().createQuery(sql, type);
        QueryResult result = queryObj.execute();
        if (result.getNodes().getSize() == 1) {
            return result.getNodes().nextNode();
        }
        return null;
    }

    public Node findOneQueryNoArgs(Session session, String sql, String type) throws RepositoryException {
        Query queryObj = session.getWorkspace().getQueryManager().createQuery(sql, type);
        QueryResult result = queryObj.execute();
        if (result.getNodes().getSize() == 1) {
            return result.getNodes().nextNode();
        }
        return null;
    }

    public Node findOneXPath(Session session, String xpath) throws RepositoryException {
        Query queryObj = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        QueryResult result = queryObj.execute();
        if (result.getNodes().getSize() >= 1) {
            return result.getNodes().nextNode();
        }
        return null;
    }

    public Node mostRecentDraft(Node handle) throws RepositoryException {
        NodeIterator it = handle.getNodes();
        Node node = null;
        while (it.hasNext()) {
            node = it.nextNode();
        }
        return node;
    }

    public void sortChildren(Node node, boolean reverse) throws RepositoryException {
        List<String> sortedNames = sortedNames(node.getNodes());
        if (reverse) {
            Collections.reverse(sortedNames);
        }
        for (int i = sortedNames.size() - 1; i >= 0; i--) {
            String before = sortedNames.get(i);
            String after = i < sortedNames.size() - 1 ? sortedNames.get(i + 1) : null;
            node.orderBefore(before, after);
        }
    }

    /**
     * Sort the nodes in an iterator, Folders in alphabetical order first then other documents in alphabetical order.
     */
    private List<String> sortedNames(NodeIterator it) throws RepositoryException {

        List<String> folders = new ArrayList<>();
        List<String> others = new ArrayList<>();
        while (it.hasNext()) {
            Node next = it.nextNode();
            if (isHippoFolder(next)) {
                folders.add(next.getName());
            } else {
                others.add(next.getName());
            }
        }
        folders.sort(String::compareToIgnoreCase);
        others.sort(String::compareToIgnoreCase);
        List<String> names = new ArrayList<>();
        names.addAll(folders);
        names.addAll(others);
        return names;
    }

    public boolean isHippoFolder(Node node) throws RepositoryException {
        return "hippostd:folder".equals(node.getPrimaryNodeType().getName());
    }

    public void createMirror(Node publicationNode, String propertyName, Node handle) throws RepositoryException {
        Node mirror = createNode(publicationNode, propertyName, "hippo:mirror");
        mirror.setProperty("hippo:docbase", handle.getIdentifier());
    }

    public long executeXpathQuery(
            Session session,
            String query,
            ThrowingConsumer consumer) throws RepositoryException {
        return executeQuery(session, query, Query.XPATH, node -> true, consumer);
    }

    public long executeQuery(
            Session session,
            String query,
            String queryType,
            ThrowingPredicate predicate,
            ThrowingConsumer consumer) throws RepositoryException {
        Query queryObj = session
                .getWorkspace()
                .getQueryManager()
                .createQuery(query, queryType);
        queryObj.setLimit(250000);
        QueryResult result = queryObj.execute();
        NodeIterator nodeIt = result.getNodes();
        while (nodeIt.hasNext()) {
            Node node = nodeIt.nextNode();
            if (predicate.test(node)) {
                consumer.accept(node);
            }
        }
        return nodeIt.getSize();
    }
}
