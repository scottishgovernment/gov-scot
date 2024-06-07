package scot.gov.pressreleases;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.BadRequestException;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class ValueListImporter {

    private static final String LIST_ITEM = "selection:listitem";

    Set<String> supportedValueLists = new HashSet<>(Arrays.asList("policyTags", "topicTags"));

    public void importValueList(Session session, String name, Map<String, String> values) throws RepositoryException {

        if (!supportedValueLists.contains(name)) {
            throw new BadRequestException("unsupported value list:" + name);
        }

        Node valueLists = session.getNode("/content/documents/govscot/valuelists");
        Node valueList = valueLists.getNode(name).getNode(name);
        Map<String, String> existingValues = getValueListAsMap(session, name);
        existingValues.putAll(values);
        LinkedHashMap<String, String> sortedMap = existingValues.entrySet()
                .stream().sorted(Map.Entry.comparingByValue())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        removeChildren(valueList);
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            createValueListNode(valueList, entry.getKey(), entry.getValue());
        }
        session.save();
    }

    void createValueListNode(Node node, String key, String value) throws RepositoryException {
        Node listItemNode = createNode(node, LIST_ITEM, LIST_ITEM);
        listItemNode.setProperty("selection:key", key);
        listItemNode.setProperty("selection:label", value);
    }

    private Node createNode(Node parent, String name, String primaryType, String ...mixins) throws RepositoryException {
        Node node = parent.addNode(name, primaryType);
        for (String mixin : mixins) {
            node.addMixin(mixin);
        }
        return node;
    }

    static Map<String, String> getValueListAsMap(Session session, String valueList) throws RepositoryException {
        Map<String, String> map = new HashMap<>();
        String path = String.format("/content/documents/govscot/valuelists/%s/%s", valueList, valueList);
        Node topicTags = session.getNode(path);
        NodeIterator it = topicTags.getNodes(LIST_ITEM);
        while (it.hasNext()) {
            Node n = it.nextNode();
            map.put(
                    n.getProperty("selection:key").getString(),
                    n.getProperty("selection:label").getString());
        }
        return map;
    }

    void removeChildren(Node node) throws RepositoryException {
        NodeIterator nodeIt = node.getNodes();
        while (nodeIt.hasNext()) {
            nodeIt.nextNode().remove();
        }
    }
}
