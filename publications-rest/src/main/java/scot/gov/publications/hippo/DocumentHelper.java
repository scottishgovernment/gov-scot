package scot.gov.publications.hippo;

import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.metadata.Metadata;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Created by z418868 on 14/07/2020.
 */
public class DocumentHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentHelper.class);

    Session session;

    HippoNodeFactory nodeFactory;

    HippoUtils hippoUtils = new HippoUtils();


    public DocumentHelper(Session session) {
        this.session = session;
        this.nodeFactory = new HippoNodeFactory(session);
    }

    public PublicationState ensurePublicationState(
            DocumentManager documentManager,
            Node node,
            String path,
            Metadata metadata)
            throws RepositoryException {

        // reconcile embargo fields before deciding whether to publish/depublish - a publication
        // embargoed on an earlier import (publication date then in the future) can have its
        // publication date pass into the past by a later re-import, and the embargo plugin blocks
        // the publish workflow action while its embargo:handle mixin is still present, so that
        // stale mixin has to be cleared first or the publishDocument() call below fails with
        // "doesn't have publish action".
        boolean embargoed = metadata.shoudlEmbargo();
        nodeFactory.ensureEmbargoAndWorkflowJobs(node, metadata.getPublicationDateWithTimezone(), embargoed);

        boolean isCurrentlyPublished = hippoUtils.hasPublishedVariant(node);
        boolean passedPublicationTime = metadata.getPublicationDateWithTimezone().isBefore(ZonedDateTime.now());

        // always (re)publish once the publication date has passed, not just the first time - otherwise
        // re-uploading an already-published publication leaves its new content sitting on the unpublished
        // variant without ever reaching live, since commitEditableDocument only updates unpublished.
        //
        // but only if the workflow actually offers "publish" - it does not when re-importing a zip
        // that is identical to what is already live, since there is then nothing new to push live,
        // and calling publishDocument() regardless throws "doesn't have publish action".
        if (passedPublicationTime) {
            if (canPublish(documentManager, node, path)) {
                documentManager.publishDocument(path);
            } else {
                LOG.debug("Publish not available for {}, assuming it is already up to date", path);
            }
        } else if (isCurrentlyPublished) {
            documentManager.depublishDocument(path);
        }

        boolean published = hippoUtils.hasPublishedVariant(node);
        return new PublicationState(embargoed, !passedPublicationTime, published);
    }

    private boolean canPublish(DocumentManager documentManager, Node node, String path) throws RepositoryException {
        try {
            DocumentWorkflow workflow = documentManager.getDocumentWorkflow(node);
            Map<String, Serializable> hints = workflow.hints();
            return !Boolean.FALSE.equals(hints.get("publish"));
        } catch (WorkflowException | RemoteException e) {
            LOG.warn("Failed to read publish hints for {}, assuming publish is available", path, e);
            return true;
        }
    }
}
