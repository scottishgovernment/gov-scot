package scot.gov.www.pressreleases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.pressreleases.prgloo.PRGlooClient;
import scot.gov.www.pressreleases.prgloo.PRGlooException;
import scot.gov.www.pressreleases.health.ImporterStatus;
import scot.gov.www.pressreleases.health.ImporterStatusUpdater;
import scot.gov.www.pressreleases.prgloo.rest.Classification;
import scot.gov.www.pressreleases.prgloo.rest.TagGroup;
import scot.gov.www.pressreleases.prgloo.rest.TagGroups;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.BadRequestException;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class TagImporter {

    private static final Logger LOG = LoggerFactory.getLogger(TagImporter.class);

    private static final String LIST_ITEM = "selection:listitem";

    Set<String> supportedValueLists = new HashSet<>(Arrays.asList("policyTags", "topicTags"));

    Session session;

    ImporterStatusUpdater statusUpdater = new ImporterStatusUpdater();

    public TagImporter(Session session) {
        this.session = session;
    }

    void doImport() throws RepositoryException {

        ImporterStatus importerStatus = statusUpdater.getStatus("tag-importer", session);
        importerStatus.setLastrun(ZonedDateTime.now());
        PRGlooClient prgloo = new PRGlooClient();
        try {
            TagGroups tagGroups = fetchTags(prgloo);
            if (tagGroups.getTagGroupList().isEmpty()) {
                LOG.error("Tags have not been returned in the PRgloo API");
                return;
            }

            TagGroup topicsGroup = tagGroups.getTopicsTagGroup();
            if (topicsGroup != null) {
                importValueList(session, "topicTags", newsTags(topicsGroup.getTags()));
            }

            TagGroup policyGroup = tagGroups.getPolicyTagGroup();
            if (policyGroup != null) {
                importValueList(session, "policyTags", newsTags(policyGroup.getTags()));
            }
            importerStatus.setLastSuccessfulRun(importerStatus.getLastrun());
        } catch (PressReleaseImporterException e) {
            LOG.error("Exception thrown", e);
            importerStatus.setSuccess(false);
            importerStatus.setMessage(e.getMessage());
        } finally {
            statusUpdater.saveStatus(importerStatus, session);
            prgloo.close();
            session.save();
        }
    }

    TagGroups fetchTags(PRGlooClient prgloo) {
        LOG.info("Fetching tags");

        try {
            TagGroups tagGroups = prgloo.tagGroups();
            if (tagGroups == null) {
                throw new PressReleaseImporterException("failed to get tags");
            }
            LOG.info("Found {} tag groups", tagGroups.getTagGroupList().size());
            return tagGroups;
        } catch (PRGlooException e) {
            throw new PressReleaseImporterException("failed to get tags", e);
        }
    }

    Map<String, String> newsTags(List<Classification> values) {
        Map<String, String> types = new HashMap<>();
        values.stream().forEach(map -> types.put(map.getId(), map.getName()));
        return types;
    }

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
