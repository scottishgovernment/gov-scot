package scot.gov.publications.hippo;

import javax.jcr.*;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import static scot.gov.publications.hippo.Constants.*;

public class HippoNodeFactory {

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    public HippoNodeFactory(Session session) {
        this.session = session;
    }

    /**
     * Create a bare handle + document variant directly via JCR, bypassing the generic
     * content-exim/FolderWorkflow "add document" mechanism entirely.
     *
     * The folder a new publication is created in deliberately has an empty hippostd:foldertype
     * (see PublicationNodeUpdater.path()) because, per the CMS's own new-publication-folder
     * template, its "index" document is meant to be created atomically as part of that template
     * rather than added afterwards - so content-exim's WorkflowDocumentManagerImpl.createDocument(),
     * which always asks FolderWorkflow to add under a hardcoded "new-document" category, has
     * nothing to match and fails. Creating the bare node here and leaving ContentNode binding to
     * WorkflowDocumentVariantImportTask's *update* path (which works against an already-existing
     * node, exactly as it already does for re-imported publications) avoids that mechanism.
     */
    public Node newBarePublicationVariant(Node publicationFolder, String slug, String type, String title)
            throws RepositoryException {
        Node handle = hippoUtils.createNode(publicationFolder, slug, "hippo:handle", HANDLE_MIXINS);
        handle.setProperty(HIPPO_NAME, TitleSanitiser.sanitise(title));

        Node variant = hippoUtils.createNode(handle, slug, type, DOCUMENT_MIXINS);
        variant.setProperty("hippotranslation:locale", "en");
        variant.setProperty("hippotranslation:id", UUID.randomUUID().toString());
        Calendar now = Calendar.getInstance();
        variant.setProperty("hippostdpubwf:createdBy", session.getUserID());
        variant.setProperty("hippostdpubwf:creationDate", now);
        variant.setProperty("hippostdpubwf:lastModifiedBy", session.getUserID());
        variant.setProperty("hippostdpubwf:lastModificationDate", now);
        variant.setProperty(HIPPO_AVAILABILITY, new String[]{"preview"});
        variant.setProperty(HIPPOSTD_STATE, "unpublished");
        return handle;
    }

    public void ensureEmbargoAndWorkflowJobs(Node handle, ZonedDateTime publishDateTime, boolean embargo) throws RepositoryException {

        ensureWorkflowJobsDeleted(handle);
        ensureEmbargoFields(handle, publishDateTime, embargo);
        ensureWorkflowJob(handle, publishDateTime);
    }

    /**
     * Ensure the right values used by the embargo plugin are used for this node.
     *
     * If the node is supposed to be embargoed then:
     * - the handle needs a mixin
     * - the handle needs a property indicating the embargo group.
     * - the node needs a mixin
     * - we also need a workflow job that will remove the embargo fields at the publish date and time
     */
    void ensureEmbargoFields(Node handle, ZonedDateTime publishDateTime, boolean embargo) throws RepositoryException {

        if (embargo) {
            hippoUtils.apply(handle.getNodes(), child -> child.addMixin(EMBARGO_DOCUMENT));
            handle.addMixin(EMBARGO_HANDLE);
            handle.setProperty(EMBARGO_GROUPS, new String[]{"general-embargo"});
            createRemoveEmbargoJob(handle, publishDateTime);
        } else {
            hippoUtils.apply(handle.getNodes(), child -> hippoUtils.ensureMixinRemoved(child, EMBARGO_DOCUMENT));
            hippoUtils.ensureMixinRemoved(handle, EMBARGO_HANDLE);
            hippoUtils.ensurePropertyRemoved(handle, EMBARGO_GROUPS);
        }
    }

    public void createRemoveEmbargoJob(Node handle, ZonedDateTime publishDateTime) throws RepositoryException {

        // create the job needed to publish this node
        Node job = handle.addNode(EMBARGO_REQUEST, "embargo:job");
        job.addMixin(MIX_REFERENCEABLE);
        job.addMixin(MIX_SIMPLE_VERSIONABLE);
        job.addMixin(MIX_VERSIONABLE);
        job.setProperty(HIPPOSCHED_ATTRIBUTE_NAMES, new String[] { HIPPOSCHED_SUBJECT_ID, HIPPOSCHED_METHOD_NAME});
        job.setProperty(HIPPOSCHED_ATTRIBUTE_VALUES, new String[] { handle.getIdentifier(), "removeEmbargo"});

        job.setProperty(HIPPOSCHED_REPOSITORY_JOB_CLASS,
                "org.onehippo.forge.embargo.repository.workflow.EmbargoWorkflowImpl$WorkflowJob");
        Node triggers = job.addNode(HIPPOSCHED_TRIGGERS, HIPPOSCHED_TRIGGERS);
        Node defaultNode = triggers.addNode("embargo", HIPPOSCHED_SIMPLE_TRIGGER);
        defaultNode.addMixin(MIX_LOCKABLE);
        defaultNode.addMixin(MIX_REFERENCEABLE);

        Calendar publishTime = GregorianCalendar.from(publishDateTime);
        defaultNode.setProperty(HIPPOSCHED_NEXT_FIRE_TIME, publishTime);
        defaultNode.setProperty(HIPPOSCHED_START_TIME, publishTime);
    }

    /**
     * If this publication node has a workflow job or embargo removal job attached to its handle then remove them.
     * Both must be cleared together, otherwise a stale embargo:request job left over from a previous import can
     * fire its removeEmbargo action while a later scheduled publish job is versioning the same handle, causing a
     * JCR NoSuchItemStateException on embargo:groups.
     */
    public void ensureWorkflowJobsDeleted(Node handle) throws RepositoryException {
        removeChildNodes(handle, HIPPO_REQUEST);
        removeChildNodes(handle, EMBARGO_REQUEST);
    }

    private void removeChildNodes(Node handle, String name) throws RepositoryException {
        NodeIterator it = handle.getNodes(name);
        while (it.hasNext()) {
            Node request = (Node) it.nextNode();
            request.remove();
        }
    }

    /**
     * Create a workflow job to publish this publication at the specified publication date
     */
    public void ensureWorkflowJob(Node handle, ZonedDateTime publishDateTime) throws RepositoryException {
        boolean needsJob = publishDateTime.isAfter(ZonedDateTime.now());

        if (!needsJob) {
            return;
        }

        // create the job needed to publish this node
        Node job = handle.addNode(HIPPO_REQUEST, "hipposched:workflowjob");
        job.setProperty(HIPPOSCHED_ATTRIBUTE_NAMES, new String[] { HIPPOSCHED_SUBJECT_ID, HIPPOSCHED_METHOD_NAME});
        job.setProperty(HIPPOSCHED_ATTRIBUTE_VALUES, new String[] { handle.getIdentifier(), "publish"});
        job.setProperty(HIPPOSCHED_REPOSITORY_JOB_CLASS,
                "org.onehippo.repository.documentworkflow.task.ScheduleWorkflowTask$WorkflowJob");
        Node triggers = job.addNode(HIPPOSCHED_TRIGGERS, HIPPOSCHED_TRIGGERS);
        Node defaultNode = triggers.addNode("default", HIPPOSCHED_SIMPLE_TRIGGER);
        defaultNode.addMixin(MIX_LOCKABLE);
        defaultNode.addMixin(MIX_REFERENCEABLE);
        Calendar publishTime = GregorianCalendar.from(publishDateTime);
        defaultNode.setProperty(HIPPOSCHED_NEXT_FIRE_TIME, publishTime);
        defaultNode.setProperty(HIPPOSCHED_START_TIME, publishTime);
    }

}
