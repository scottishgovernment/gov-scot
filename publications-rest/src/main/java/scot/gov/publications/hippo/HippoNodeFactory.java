package scot.gov.publications.hippo;

import javax.jcr.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static scot.gov.publications.hippo.Constants.*;

public class HippoNodeFactory {

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    public HippoNodeFactory(Session session) {
        this.session = session;
    }

    public Node newHandle(String title, Node parent, String slug) throws RepositoryException {
        Node handle = hippoUtils.createNode(parent, slug, "hippo:handle", HANDLE_MIXINS);
        handle.setProperty(HIPPO_NAME, TitleSanitiser.sanitise(title));
        return handle;
    }

    public Node newDocumentNode(
            Node handle,
            String slug,
            String title,
            String type,
            ZonedDateTime publishDateTime,
            boolean embargo) throws RepositoryException {

        Node node = hippoUtils.createNode(handle, slug, type, DOCUMENT_MIXINS);

        // the folder has been created using the SlugAllocations strategy so we can use it name as the govscot:slug
        // property that is used to determine the url of this publication.
        node.setProperty("govscot:slug", handle.getParent().getName());
        node.setProperty(HIPPO_NAME, TitleSanitiser.sanitise(title));
        node.setProperty("hippotranslation:locale", "en");
        node.setProperty("hippotranslation:id", UUID.randomUUID().toString());
        Calendar now = Calendar.getInstance();
        node.setProperty("hippostdpubwf:createdBy", "aps-importer");
        node.setProperty("hippostdpubwf:creationDate", now);
        node.setProperty("hippostdpubwf:lastModifiedBy", "aps-importer");
        node.setProperty("hippostdpubwf:lastModificationDate", now);
        ensurePublicationStatus(node, publishDateTime, embargo);
        return node;
    }

    /**
     * Ensure that the publication status and wny required workflow job exists for this publication
     */
    public void ensurePublicationStatus(Node node, ZonedDateTime publishDateTime, boolean embargo) throws RepositoryException {
        // remove any workflow job that exists.  If it is needed then we will recreate it. Ditto for embargo jobs
        ensureWorkflowJobsDeleted(node.getParent());
        ensureEmbargoFields(node, publishDateTime, embargo);

        if (publishDateTime.isBefore(ZonedDateTime.now())) {
            // the publication can be published
            node.setProperty(HIPPO_AVAILABILITY, new String[]{"live", "preview"});
            node.setProperty(HIPPOSTD_STATE, "published");
            node.setProperty(HIPPOSTD_STATE_SUMMARY, "live");
        } else {
            node.setProperty(HIPPO_AVAILABILITY, new String[]{"preview"});
            node.setProperty(HIPPOSTD_STATE, "unpublished");
            node.setProperty(HIPPOSTD_STATE_SUMMARY, "new");

            // If the publish date is in the future, we need to create an unpublished,
            // preview node. These nodes should have the "versionable" mixin. This node
            // is still present once the live, published node is created. If this mixin
            // isn't set, it causes exceptions when a page or document is unpublished.
            node.addMixin(MIX_VERSIONABLE);
            ensureWorkflowJob(node.getParent(), publishDateTime);
        }
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
     * - the handle needs a roperty indicating the embargo group.
     * - the node needs a mixin
     * - we also need a workflow job that will remove the embargo fields at the publish date and tiem
     * In the case wher
     */
    void ensureEmbargoFields(Node handle, ZonedDateTime publishDateTime, boolean embargo) throws RepositoryException {

        if (embargo) {
            hippoUtils.apply(handle.getNodes(), child -> child.addMixin(EMBARGO_DOCUMENT));
            handle.addMixin(EMBARGO_HANDLE);
            handle.setProperty(EMBARGO_GROUPS, new String[]{"General Embargo"});
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
        job.setProperty("hipposched:attributeNames", new String[] { "hipposched:subjectId", "hipposched:methodName"});
        job.setProperty("hipposched:attributeValues", new String[] { handle.getIdentifier(), "removeEmbargo"});

        job.setProperty("hipposched:repositoryJobClass",
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
     * If this publication node has a workflow job attached to its handle then remove it
     */
    public void ensureWorkflowJobsDeleted(Node handle) throws RepositoryException {
        NodeIterator it = handle.getNodes(HIPPO_REQUEST);
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
        job.setProperty("hipposched:attributeNames", new String[] { "hipposched:subjectId", "hipposched:methodName"});
        job.setProperty("hipposched:attributeValues", new String[] { handle.getIdentifier(), "publish"});
        job.setProperty("hipposched:repositoryJobClass",
                "org.onehippo.repository.documentworkflow.task.ScheduleWorkflowTask$WorkflowJob");
        Node triggers = job.addNode(HIPPOSCHED_TRIGGERS, HIPPOSCHED_TRIGGERS);
        Node defaultNode = triggers.addNode("default", HIPPOSCHED_SIMPLE_TRIGGER);
        defaultNode.addMixin(MIX_LOCKABLE);
        defaultNode.addMixin(MIX_REFERENCEABLE);
        Calendar publishTime = GregorianCalendar.from(publishDateTime);
        defaultNode.setProperty(HIPPOSCHED_NEXT_FIRE_TIME, publishTime);
        defaultNode.setProperty(HIPPOSCHED_START_TIME, publishTime);
    }

    public Node newResourceNode(
            Node parent,
            String property,
            String filename,
            String contentType,
            ZipFile zipFile, ZipEntry zipEntry)
            throws RepositoryException {

        try {
            Node resourceNode = parent.addNode(property, "hippo:resource");
            Binary binary = session.getValueFactory().createBinary(zipFile.getInputStream(zipEntry));

            resourceNode.setProperty(HIPPO_FILENAME, filename);
            resourceNode.setProperty(JCR_DATA, binary);
            resourceNode.setProperty(JCR_MIMETYPE, contentType);
            resourceNode.setProperty(JCR_LAST_MODIFIED, Calendar.getInstance());

            // to avoid costly text extraction, set hippo:text to an empty string
            resourceNode.setProperty("hippo:text", "");
            return resourceNode;
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    public void addBasicFields(Node node, String name) throws RepositoryException {
        hippoUtils.setPropertyIfAbsent(node, "hippo:name", name);
        node.setProperty("hippotranslation:locale", "en");
        node.setProperty("hippotranslation:id", UUID.randomUUID().toString());
        Calendar now = Calendar.getInstance();
        node.setProperty("hippostdpubwf:createdBy", "aps-importer");
        node.setProperty("hippostdpubwf:creationDate", now);
        hippoUtils.setPropertyIfAbsent(node, "hippostdpubwf:lastModifiedBy", "aps-importer");
        node.setProperty("hippostdpubwf:lastModificationDate", now);
    }

}
