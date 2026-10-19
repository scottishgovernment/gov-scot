package scot.gov.publications.hippo;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.ISO8601;
import org.hippoecm.repository.api.Document;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantExportTask;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.forge.content.pojo.model.ContentPropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.metadata.Consultation;
import scot.gov.publications.metadata.ConsultationResponseMethod;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.metadata.Update;
import scot.gov.publications.repo.Publication;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static scot.gov.publications.hippo.Constants.GOVSCOT_CONTENT;
import static scot.gov.publications.hippo.Constants.HIPPOSTD_FOLDERTYPE;
import static scot.gov.publications.hippo.Constants.HIPPOSTD_RELAXED;
import static scot.gov.publications.hippo.Constants.HIPPOSTD_CONTAINER;
import static scot.gov.publications.hippo.Constants.HIPPO_CONTAINER;
import static scot.gov.publications.hippo.Constants.HIPPO_DOCBASE;
import static scot.gov.publications.hippo.XpathQueryHelper.*;

/**
 * Responsible for creating and updating publication nodes in Hippo based on the metedata from an APS  zip file.
 */
public class PublicationNodeUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationNodeUpdater.class);

    private static final String RESPONSIBLE_DIRECTORATE = "govscot:responsibleDirectorate";

    private static final String RESPONSIBLE_ROLE = "govscot:responsibleRole";

    private static final String SECONDARY_RESPONSIBLE_DIRECTORATE = "govscot:secondaryResponsibleDirectorate";

    private static final String SECONDARY_RESPONSIBLE_ROLE = "govscot:secondaryResponsibleRole";

    private static final String FEATURED_LINKS = "govscot:featuredLinks";

    private static final String CONSULTATION_RESPONSE_METHODS = "govscot:consultationResponseMethods";

    private static final String LINK = "govscot:link";

    // a hippo:docbase of all zeroes/placeholder characters denotes an unset mirror link
    private static final String EMPTY_DOCBASE = "cafebabe-cafe-babe-cafe-babecafebabe";

    private static final String UPDATE_HISTORY = "govscot:updateHistory";

    private static final String LAST_UPDATED = "govscot:lastUpdated";

    private static final String UPDATE_TEXT = "govscot:updateText";

    private static final String BULKPUBLISH_DIRECTORY = "bulkpublish:bulkpublishdirectory";

    Session session;

    HippoPaths hippoPaths;

    HippoNodeFactory nodeFactory;

    TopicsUpdater topicMappings;

    PoliciesUpdater policiesUpdater;

    PublicationPathStrategy pathStrategy;

    HippoUtils hippoUtils = new HippoUtils();

    TagUpdater tagUpdater = new TagUpdater();

    DocumentHelper documentHelper;

    // the folder most recently created/ensured by path(Metadata) - stashed there (rather than
    // re-resolved via session.getNode on the string path() returns) since hippoPaths.ensurePath()
    // can legitimately slugify a path element differently to the pre-computed string, and this
    // instance is single-use per import so there is no concurrency/reentrancy concern.
    private Node lastEnsuredPublicationFolder;

    // the embargo/scheduling/publish state determined for the publication's own index document by
    // the most recent createOrUpdatePublicationNode() call - exposed to the caller since this
    // instance is single-use per import.
    private PublicationState lastPublicationState;

    public PublicationState getLastPublicationState() {
        return lastPublicationState;
    }

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
        Node publicationFolder;

        if (pubNode == null) {
            path = path(metadata);
            publicationFolder = lastEnsuredPublicationFolder;
            String slug = StringUtils.substringAfterLast(path, "/");
            path = path + "/index";

            // pre-create a bare handle + variant via raw JCR rather than relying on
            // WorkflowDocumentVariantImportTask to create it (see newBarePublicationVariant javadoc
            // for why that doesn't work here) - this lets the import task below find an existing
            // document at this path and go straight to its (working) update path instead.
            if (!publicationFolder.hasNode("index")) {
                nodeFactory.newBarePublicationVariant(publicationFolder, "index", "govscot:Publication", metadata.getTitle());
                session.save();
            }

            contentNode = new ContentNodes().newContentNode(metadata, publication, slug);
        } else {
            LOG.debug("updating existing publication node {}, handle {}", pubNode.getPath(), pubNode.getParent().getPath());
            nodeFactory.ensureWorkflowJobsDeleted(pubNode.getParent());
            session.save();
            path = pubNode.getParent().getPath();
            publicationFolder = pubNode.getParent().getParent();
            WorkflowDocumentVariantExportTask exportTask = new WorkflowDocumentVariantExportTask(documentManager);
            Document doc = new Document(pubNode.getIdentifier());
            contentNode = exportTask.exportVariantToContentNode(doc);
            contentNode.setProperty("govscot:publicationType", hippoPaths.slugify(metadata.mappedPublicationType(), false));
            new ContentNodes().updateContentNode(contentNode, metadata);
        }

        // ensure the folder has the mixin used to let it be included in bulk publish actions
        if (!publicationFolder.isNodeType(BULKPUBLISH_DIRECTORY)) {
            publicationFolder.addMixin(BULKPUBLISH_DIRECTORY);
        }

        try {
            topicMappings.ensureTopics(contentNode, metadata);
            maintainUpdateHistory(contentNode, metadata);
            createDirectoratesIfAbsent(contentNode, metadata);
            createRolesIfAbsent(contentNode, metadata);
            createOrUpdateConsultationFields(contentNode, metadata);
            createOrUpdateConsultationAnalysisFields(contentNode, metadata);
            tagUpdater.updateTags(contentNode, metadata.getTags());

            WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
            String publishedPath =
                    importTask.createOrUpdateDocumentFromVariantContentNode(
                            contentNode, "govscot:Publication", path, "en", publication.getTitle());
            Node publicationNode = session.getNode(publishedPath);
            lastPublicationState = documentHelper.ensurePublicationState(documentManager, publicationNode, publishedPath, metadata);
            publicationNode = session.getNode(publishedPath);
            policiesUpdater.ensurePolicies(publicationNode, metadata);
            return publicationNode;
        } catch (RepositoryException | ApsZipImporterException e) {
            removePublicationFolderQuietly(publicationFolder);
            throw e;
        }
    }

    /**
     * Remove the publication folder after a failed create/update, so a partially imported
     * publication is not left behind in the repository. Any failure while doing so is logged
     * rather than propagated, since it would otherwise mask the original failure.
     */
    void removePublicationFolderQuietly(Node publicationFolder) {
        if (publicationFolder == null) {
            return;
        }
        try {
            Session folderSession = publicationFolder.getSession();
            publicationFolder.remove();
            folderSession.save();
        } catch (RepositoryException e) {
            LOG.error("Failed to remove publication folder after exception", e);
        }
    }

    String path(Metadata metadata) throws RepositoryException {
        String path = pathStrategy.pathAsString(metadata);
        List<String> pathList = Arrays.asList(substringAfter(path, "govscot/").split("/"));
        Node publicationFolder = hippoPaths.ensurePath(pathList);

        LOG.debug("ensureFolderActions {}", publicationFolder.getPath());
        Node monthFolder = publicationFolder.getParent();
        Node yearFolder = monthFolder.getParent();
        hippoUtils.setPropertyStrings(publicationFolder, HIPPOSTD_FOLDERTYPE, actions());
        hippoUtils.setPropertyStrings(monthFolder, HIPPOSTD_FOLDERTYPE, actions("new-publication-folder", "new-complex-document-folder"));
        hippoUtils.setPropertyStrings(yearFolder, HIPPOSTD_FOLDERTYPE, actions("new-publication-month-folder"));
        publicationFolder.setProperty("hippo:name", metadata.getTitle());
        session.save();
        // the import task that creates the document below resolves its own folder path (and can
        // create any missing ancestors itself), so keep returning the path as computed above
        // rather than publicationFolder.getPath() - the two can legitimately diverge in casing.
        lastEnsuredPublicationFolder = publicationFolder;
        return path;
    }

    private Collection<String> actions(String ...actions) {
        return Arrays.asList(actions);
    }

    /**
     * Add a new update history entry for the metadata's update, if any, unless an identical entry
     * already exists. Entries are always appended (never inserted mid-list) - the ContentNode tree
     * is round-tripped through an export/bind cycle for existing publications, and the content-exim
     * binder does not handle a same-name-sibling being inserted ahead of ones it already knows
     * about; doing so corrupts the JCR save (ItemNotFoundException on the next session.save()).
     */
    private void maintainUpdateHistory(ContentNode publicationNode, Metadata metadata) {

        Update update = metadata.getUpdate();

        // if there is no update then there is nothing to do
        if (update == null) {
            return;
        }

        Calendar newLastUpdated = GregorianCalendar.from(update.getLastUpdatedWithTimezone());

        // if an identical entry already exists then do nothing
        boolean alreadyExists = publicationNode.getNodes().stream()
                .filter(node -> UPDATE_HISTORY.equals(node.getName()))
                .anyMatch(entry -> isExistingEntry(entry, update, newLastUpdated));
        if (alreadyExists) {
            return;
        }

        ContentNode newUpdateEntry = new ContentNode();
        newUpdateEntry.setName(UPDATE_HISTORY);
        newUpdateEntry.setPrimaryType("govscot:UpdateHistory");
        newUpdateEntry.setProperty(LAST_UPDATED, ContentPropertyType.DATE, ISO8601.format(newLastUpdated));
        newUpdateEntry.setProperty(UPDATE_TEXT, update.getUpdateText());
        publicationNode.addNode(newUpdateEntry);
    }

    private boolean isExistingEntry(ContentNode entry, Update update, Calendar newLastUpdated) {
        Calendar oldLastUpdated = ISO8601.parse(entry.getProperty(LAST_UPDATED).getValue());
        String oldUpdateText = entry.getProperty(UPDATE_TEXT).getValue();
        return sameInstant(newLastUpdated, oldLastUpdated) && update.getUpdateText().equals(oldUpdateText);
    }

    private boolean sameInstant(Calendar left, Calendar right) {
        return left.getTimeInMillis() == right.getTimeInMillis();
    }

    /**
     * Update the publication node from the directorates in the metadata. If the node already contains information
     * it will not be overwritten with this data.  We may want to revise this later as we do not want manual edits
     * to be needed.
     */
    private void createDirectoratesIfAbsent(ContentNode publicationNode, Metadata metadata) throws RepositoryException {

        // if there is a primary responsible directorate specified, create it if absent, or repair
        // it in place if it is just an unresolved placeholder - never remove+re-add the mirror
        // ContentNode (see updateMirrorDocbase for why).
        String primaryDirectorate = metadata.getPrimaryResponsibleDirectorate();
        if (!isBlank(primaryDirectorate)) {
            if (!publicationNode.hasNode(RESPONSIBLE_DIRECTORATE)) {
                createDirectorateLink(publicationNode, RESPONSIBLE_DIRECTORATE, primaryDirectorate);
            } else {
                ContentNode respDirectorate = publicationNode.getNode(RESPONSIBLE_DIRECTORATE);
                // some nodes have a responsibleDirectorate of / ... treat these as empty
                if (EMPTY_DOCBASE.equals(respDirectorate.getProperty(HIPPO_DOCBASE).getValue())) {
                    updateMirrorDocbase(respDirectorate, directorateHandleQuery(primaryDirectorate), "directorate", primaryDirectorate);
                }
            }
        }

        if (!publicationNode.hasNode(SECONDARY_RESPONSIBLE_DIRECTORATE)) {
            for (String directorate : metadata.getSecondaryResponsibleDirectorates()) {
                createDirectorateLink(publicationNode, SECONDARY_RESPONSIBLE_DIRECTORATE, directorate);
            }
        }
    }

    /**
     * Resolve the target and update an existing mirror's docbase property in place, rather than
     * removing the ContentNode and adding a fresh one in its place - the content-exim binder does
     * not handle a same-name-sibling being removed from the ContentNode tree it exported, and
     * corrupts the JCR save with a NoSuchItemStateException when re-importing a publication whose
     * directorate/role mirror still has the empty/placeholder docbase.
     */
    private void updateMirrorDocbase(ContentNode mirrorNode, String xpath, String kind, String name) throws RepositoryException {
        Node handle = hippoUtils.findOneXPath(session, xpath);
        if (handle != null) {
            mirrorNode.setProperty(HIPPO_DOCBASE, handle.getIdentifier());
        } else {
            LOG.warn("No such {}: '{}'", kind, name);
        }
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

    /**
     * Set the opening/closing dates, derived open state and response url/methods for a consultation
     * paper, matching the publications-service importer. The response methods are rebuilt from
     * scratch on every import since they have no identity to repair in place.
     */
    private void createOrUpdateConsultationFields(ContentNode publicationNode, Metadata metadata) {
        if (!metadata.isConsultation()) {
            return;
        }

        Consultation consultation = metadata.getConsultation();
        LocalDateTime now = LocalDateTime.now();
        boolean open = now.isAfter(consultation.getOpeningDate()) && now.isBefore(consultation.getClosingDate());

        publicationNode.setProperty("govscot:openingDate", ContentPropertyType.DATE,
                ISO8601.format(GregorianCalendar.from(consultation.getOpeningDate().atZone(ZoneId.systemDefault()))));
        publicationNode.setProperty("govscot:closingDate", ContentPropertyType.DATE,
                ISO8601.format(GregorianCalendar.from(consultation.getClosingDate().atZone(ZoneId.systemDefault()))));
        publicationNode.setProperty("govscot:isOpen", ContentPropertyType.BOOLEAN, Boolean.toString(open));
        publicationNode.setProperty("govscot:responseUrl", consultation.getResponseUrl());

        // remove any response methods that exist from a previous upload and rebuild from the metadata
        publicationNode.getNodes().removeIf(node -> CONSULTATION_RESPONSE_METHODS.equals(node.getName()));
        for (ConsultationResponseMethod responseMethod : consultation.getAlternativeWaysToRespond()) {
            ContentNode responseMethodNode = new ContentNode();
            responseMethodNode.setName(CONSULTATION_RESPONSE_METHODS);
            responseMethodNode.setPrimaryType("govscot:ConsultationResponseType");
            responseMethodNode.addMixinType(HIPPO_CONTAINER);
            responseMethodNode.addMixinType(HIPPOSTD_CONTAINER);
            responseMethodNode.addMixinType(HIPPOSTD_RELAXED);
            responseMethodNode.setProperty("govscot:type", responseMethod.getTitle());
            responseMethodNode.addNode(new ContentNodes().htmlNode(GOVSCOT_CONTENT, responseMethod.getText()));
            publicationNode.addNode(responseMethodNode);
        }
    }

    /**
     * Consultation analysis publications may link back to the consultation they analyse, via a
     * featured link to the consultation's handle. Re-created on every import so stale links are
     * replaced rather than accumulated.
     */
    private void createOrUpdateConsultationAnalysisFields(ContentNode publicationNode, Metadata metadata)
            throws RepositoryException, ApsZipImporterException {

        if (!metadata.isConsultationAnalysis()) {
            return;
        }

        Consultation consultation = metadata.getConsultation();
        if (isBlank(consultation.getConsultationUrl())) {
            LOG.warn("Consultation analysis does not specify a consultation {}", metadata.getIsbn());
            return;
        }

        Node consultationHandle = getConsultationNode(consultation.getConsultationUrl());

        // update the existing featured link in place rather than removing and re-adding it - the
        // content-exim binder does not handle a same-name-sibling being removed from the
        // ContentNode tree it exported, and corrupts the JCR save with a NoSuchItemStateException.
        if (publicationNode.hasNode(FEATURED_LINKS)) {
            ContentNode mirror = publicationNode.getNode(FEATURED_LINKS).getNode(LINK).getNode(LINK);
            mirror.setProperty(HIPPO_DOCBASE, consultationHandle.getIdentifier());
            return;
        }

        ContentNode featuredLinks = new ContentNode();
        featuredLinks.setName(FEATURED_LINKS);
        featuredLinks.setPrimaryType("govscot:FeaturedLinks");
        featuredLinks.setProperty("govscot:label", "Consultation");

        ContentNode linkNode = new ContentNode();
        linkNode.setName(LINK);
        linkNode.setPrimaryType("govscot:DescribedLink");
        linkNode.setProperty("govscot:customLinkText", "");
        addMirror(linkNode, LINK, consultationHandle.getIdentifier());

        featuredLinks.addNode(linkNode);
        publicationNode.addNode(featuredLinks);
    }

    /**
     * Resolve the handle of the published, live consultation with the given gov.scot url.
     */
    Node getConsultationNode(String consultationUrl) throws RepositoryException, ApsZipImporterException {
        String slug = getSlugFromConsultationUrl(consultationUrl);
        if (slug == null) {
            throw new ApsZipImporterException("Invalid consultation URL: " + consultationUrl);
        }

        String xpath = String.format(
                "//element(*, govscot:Publication)[govscot:slug = '%s'][hippo:availability = 'live'][hippostd:state = 'published']",
                slug);
        Node consultation = hippoUtils.findOneXPath(session, xpath);
        if (consultation == null) {
            throw new ApsZipImporterException("Unable to find consultation " + consultationUrl);
        }
        return consultation.getParent();
    }

    String getSlugFromConsultationUrl(String url) {
        if (!StringUtils.startsWith(url, "https://www.gov.scot/publications/")) {
            return null;
        }
        String stripped = StringUtils.stripEnd(url, "/");
        String[] parts = stripped.split("/");
        if (parts.length != 5) {
            return null;
        }
        return parts[4];
    }

    private void createRolesIfAbsent(ContentNode publicationNode, Metadata metadata)
            throws RepositoryException, ApsZipImporterException {

        // create the primary responsible role if absent, or repair it in place if it is just an
        // unresolved placeholder - never remove+re-add the mirror ContentNode (see
        // updateMirrorDocbase for why).
        String primaryRole = metadata.getPrimaryResponsibleRole();
        if (!isBlank(primaryRole)) {
            if (!publicationNode.hasNode(RESPONSIBLE_ROLE)) {
                createRoleLink(publicationNode, RESPONSIBLE_ROLE, primaryRole);
            } else {
                repairResponsibleRoleIfPlaceholder(publicationNode, primaryRole);
            }
        }

        if (!publicationNode.hasNode(SECONDARY_RESPONSIBLE_ROLE)) {
            for (String role : metadata.getSecondaryResponsibleRoles()) {
                createRoleLink(publicationNode, SECONDARY_RESPONSIBLE_ROLE, role);
            }
        }
    }

    /**
     * If the existing responsible role mirror is just an unresolved placeholder (docbase
     * {@value #EMPTY_DOCBASE}), repair it in place with the real handle - never remove+re-add the
     * mirror ContentNode (see updateMirrorDocbase for why).
     */
    private void repairResponsibleRoleIfPlaceholder(ContentNode publicationNode, String primaryRole)
            throws RepositoryException, ApsZipImporterException {
        ContentNode respRole = publicationNode.getNode(RESPONSIBLE_ROLE);
        // some nodes have a responsibleRole of / ... treat these as empty
        if (!EMPTY_DOCBASE.equals(respRole.getProperty(HIPPO_DOCBASE).getValue())) {
            return;
        }
        Node handle = findRoleOrPerson(primaryRole);
        if (handle == null) {
            throw new ApsZipImporterException(String.format("No such role: '%s'", primaryRole));
        }
        respRole.setProperty(HIPPO_DOCBASE, handle.getIdentifier());
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
