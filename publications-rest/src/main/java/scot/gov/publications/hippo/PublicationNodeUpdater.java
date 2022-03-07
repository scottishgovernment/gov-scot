package scot.gov.publications.hippo;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.repository.api.Document;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantExportTask;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.repo.Publication;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.*;

import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static scot.gov.publications.hippo.Constants.HIPPOSTD_FOLDERTYPE;
import static scot.gov.publications.hippo.Constants.HIPPO_DOCBASE;
import static scot.gov.publications.hippo.XpathQueryHelper.*;

/**
 * Responsible for creating and updating publication nodes in Hippo based on the metedata from an APS  zip file.
 */
public class PublicationNodeUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationNodeUpdater.class);

    private static final String RESPONSIBLE_DIRECTORATE = "govscot:responsibleDirectorate";

    private static final String RESPONSIBLE_ROLE = "govscot:responsibleRole";

    Session session;

    HippoPaths hippoPaths;

    HippoNodeFactory nodeFactory;

    TopicsUpdater topicMappings;

    PoliciesUpdater policiesUpdater;

    PublicationPathStrategy pathStrategy;

    HippoUtils hippoUtils = new HippoUtils();

    TagUpdater tagUpdater = new TagUpdater();

    DocumentHelper documentHelper;

    /**
     * try and get the node
     * if it does not exist then creat a blank one
     * update the node according to the existing logic
     * use the import task to update or create the node
     */
    public PublicationNodeUpdater(Session session) {
        this.session = session;
        this.hippoPaths = new HippoPaths(session);
        this.nodeFactory = new HippoNodeFactory(session);
        this.topicMappings = new TopicsUpdater(session);
        this.pathStrategy = new PublicationPathStrategy(session);
        this.policiesUpdater = new PoliciesUpdater(session);
        this.documentHelper = new DocumentHelper(session);
    }

    /**
     * Ensure that a publications node exists containing the data contained in the metadata.
     *
     * @return Node representing the folder the publication is contained in.
     */
    public Node createOrUpdatePublicationNode(Metadata metadata, Publication publication)
            throws ApsZipImporterException {

        try {
            return doCreateOrUpdatePublicationNode(metadata, publication);
        } catch (RepositoryException e) {
            throw new ApsZipImporterException("Failed to create or update publication node", e);
        }
    }

    Node doCreateOrUpdatePublicationNode(Metadata metadata, Publication publication)
            throws RepositoryException, ApsZipImporterException {

        Node pubNode = findPublicationNodeToUpdate(metadata);

        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        ContentNode contentNode;
        String path;

        if (pubNode == null) {
            path = path(metadata);
            String slug = StringUtils.substringAfterLast(path, "/");
            path = path + "/index";
            contentNode = new ContentNodes().newContentNode(metadata, publication, slug);
        } else {
            nodeFactory.ensureWorkflowJobsDeleted(pubNode.getParent());
            session.save();
            path = pubNode.getParent().getPath();
            WorkflowDocumentVariantExportTask exportTask = new WorkflowDocumentVariantExportTask(documentManager);
            Document doc = new Document(pubNode.getIdentifier());
            contentNode = exportTask.exportVariantToContentNode(doc);
            contentNode.setProperty("govscot:publicationType", hippoPaths.slugify(metadata.mappedPublicationType(), false));
        }


        topicMappings.ensureTopics(contentNode, metadata);
        createDirectoratesIfAbsent(contentNode, metadata);
        createRolesIfAbsent(contentNode, metadata);
        tagUpdater.updateTags(contentNode, metadata.getTags());

        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
        String publishedPath =
                importTask.createOrUpdateDocumentFromVariantContentNode(
                        contentNode, "govscot:Publication", path, "en", publication.getTitle());
        Node publicationNode = session.getNode(publishedPath);
        documentHelper.ensurePublicationState(documentManager, publicationNode, publishedPath, metadata);
        publicationNode = session.getNode(publishedPath);
        policiesUpdater.ensurePolicies(publicationNode, metadata);
        return publicationNode;
    }

    String path(Metadata metadata) throws RepositoryException {
        String path = pathStrategy.pathAsString(metadata);
        List<String> pathList = Arrays.asList(substringAfter(path, "govscot/").split("/"));
        Node publicationFolder = hippoPaths.ensurePath(pathList);

        LOG.info("ensureFolderActions {}", publicationFolder.getPath());
        Node monthFolder = publicationFolder.getParent();
        Node yearFolder = monthFolder.getParent();
        hippoUtils.setPropertyStrings(publicationFolder, HIPPOSTD_FOLDERTYPE, actions());
        hippoUtils.setPropertyStrings(monthFolder, HIPPOSTD_FOLDERTYPE, actions("new-publication-folder", "new-complex-document-folder"));
        hippoUtils.setPropertyStrings(yearFolder, HIPPOSTD_FOLDERTYPE, actions("new-publication-month-folder"));
        publicationFolder.setProperty("hippo:name", metadata.getTitle());
        session.save();
        return path;
    }

    private Collection<String> actions(String ...actions) {
        return Arrays.asList(actions);
    }

    /**
     * Update the publication node from the directorates in the metadata. If the node already contains information
     * it will not be overwritten with this data.  We may want to revise this later as we do not want manual edits
     * to be needed.
     */
    private void createDirectoratesIfAbsent(ContentNode publicationNode, Metadata metadata) throws RepositoryException {

        // if there is a primary responsible directorate specified and none existing on the node then create it
        if (shouldUpdateDirectorate(publicationNode, metadata)) {
            createDirectorateLink(publicationNode, RESPONSIBLE_DIRECTORATE , metadata.getPrimaryResponsibleDirectorate());
        }

        if (!publicationNode.hasNode("govscot:secondaryResponsibleDirectorate")) {
            for (String directorate : metadata.getSecondaryResponsibleDirectorates()) {
                createDirectorateLink(publicationNode, "govscot:secondaryResponsibleDirectorate", directorate);
            }
        }
    }

    private boolean shouldUpdateDirectorate(ContentNode publicationNode, Metadata metadata) {
        if (isBlank(metadata.getPrimaryResponsibleDirectorate())) {
            return false;
        }

        if (!publicationNode.hasNode(RESPONSIBLE_DIRECTORATE )) {
            return true;
        }

        // some nodes have a responsibleDirectorate of / ... treat these as empty
        ContentNode respDirectorate = publicationNode.getNode(RESPONSIBLE_DIRECTORATE );
        if ("cafebabe-cafe-babe-cafe-babecafebabe".equals(respDirectorate.getProperty(HIPPO_DOCBASE).getValue())) {
            publicationNode.getNodes().remove(respDirectorate);
            return true;
        }

        return false;
    }

    private void createDirectorateLink(
            ContentNode publicationNode,
            String propertyName,
            String directorate) throws RepositoryException {
        Node handle = hippoUtils.findOneXPath(session, directorateHandleQuery(directorate));
        if (handle != null) {
            addMirror(publicationNode, propertyName, handle.getIdentifier());
        } else {
            LOG.warn("No such directorate: '{}'", directorate);
        }
    }

    private void addMirror(ContentNode node, String propertyName, String id) {
        ContentNode mirrorNode = new ContentNode();
        mirrorNode.setName(propertyName);
        mirrorNode.setPrimaryType("hippo:mirror");
        mirrorNode.setProperty(HIPPO_DOCBASE, id);
        node.addNode(mirrorNode);
    }

    private void createRolesIfAbsent(ContentNode publicationNode, Metadata metadata)
            throws RepositoryException, ApsZipImporterException {
        if (shouldUpdateRole(publicationNode, metadata)) {
            createRoleLink(publicationNode, RESPONSIBLE_ROLE, metadata.getPrimaryResponsibleRole());
        }

        if (!publicationNode.hasNode("govscot:secondaryResponsibleRole")) {
            for (String role : metadata.getSecondaryResponsibleRoles()) {
                createRoleLink(publicationNode, "govscot:secondaryResponsibleRole", role);
            }
        }
    }

    private boolean shouldUpdateRole(ContentNode publicationNode, Metadata metadata) {
        if (isBlank(metadata.getPrimaryResponsibleRole())) {
            return false;
        }

        if (!publicationNode.hasNode(RESPONSIBLE_ROLE)) {
            return true;
        }

        // some nodes have a responsibleDirectorate of / ... treat these as empty
        ContentNode respRole = publicationNode.getNode(RESPONSIBLE_ROLE);
        if ("cafebabe-cafe-babe-cafe-babecafebabe".equals(respRole.getProperty(HIPPO_DOCBASE).getValue())) {
            publicationNode.getNodes().remove(respRole);
            return true;
        }

        return false;
    }

    private void createRoleLink(ContentNode publicationNode, String propertyName, String title)
            throws RepositoryException, ApsZipImporterException {
        Node handle = findRoleOrPerson(title);
        if (handle != null) {
            addMirror(publicationNode, propertyName, handle.getIdentifier());
        } else {
            throw new ApsZipImporterException(String.format("No such role: '%s'", title));
        }
    }

    private Node findRoleOrPerson(String roleOrPerson) throws RepositoryException {
        return firstNonNull(
                hippoUtils.findOneXPath(session, roleHandleQuery(roleOrPerson)),
                hippoUtils.findOneXPath(session, personHandleQuery(roleOrPerson)),
                hippoUtils.findOneXPath(session, featuredRoleHandleQuery(roleOrPerson)));
    }

    /**
     * If a publication with this isbn already exists then we want to update it.  To make sure we update the right
     * node we want to find all of then and then decide which node to use if there are multiple drafts.  If a
     * published node exists then use that. Then fall back to using the unpublished one and then finally to draft.
     */
    private Node findPublicationNodeToUpdate(Metadata metadata) throws RepositoryException {
        // Query to see if a publications with this ISBN already exist.  If it does then we will update the existing
        // node rather than create a new one
        String sql = String.format("SELECT * FROM govscot:Publication WHERE govscot:isbn = '%s'", metadata.normalisedIsbn());
        Query query = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
        QueryResult result = query.execute();
        return hippoUtils.getVariant(result.getNodes());
    }

}
