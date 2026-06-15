package scot.gov.publications.hippo;

import org.hippoecm.repository.api.Document;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.DocumentManagerException;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.metadata.Metadata;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Update relationships with policies based on the metadata.
 */
public class PoliciesUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(PoliciesUpdater.class);

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    public PoliciesUpdater(Session session) {
        this.session = session;
    }

    /**
     * Ensure that all policies mentioned in the metadata link to this publication.
     */
    public void ensurePolicies(Node publicationNode, Metadata metadata) throws RepositoryException {
        for (String policy : metadata.getPolicies()) {
            ensurePolicy(publicationNode, policy);
        }
    }

    private void ensurePolicy(Node publicationNode, String policy) throws RepositoryException {

        LOG.debug("ensurePolicy {} -> {} ", policy, publicationNode.getPath());

        // find the policy latest node
        Node policyLatest = findPolicyLatestNode(policy);

        if (policyLatest == null) {
            LOG.warn("Unable to find policy latest page for policy {}", policy);
            return;
        }

        Node policyLatestHandle = policyLatest.getParent();
        Node publicationHandle = publicationNode.getParent();
        LOG.debug("publication node is {}", publicationNode.getPath());
        LOG.debug("publication handle is {}", publicationHandle.getPath());
        if (alreadyRelated(policyLatest, publicationHandle)) {
            LOG.debug("Policy is already related to {}", publicationNode.getPath());
        } else {
            linkPublicationToPolicy(policyLatestHandle, publicationHandle);
        }
    }

    // goes through the normal document workflow (obtain draft, add the link, commit, publish) rather
    // than raw-writing every same-name-sibling variant directly: this properly fails (rather than
    // silently colliding) if another editor already has the policy page checked out, and keeps the
    // published variant in sync the same way DocumentHelper does for the publication's own document.
    private void linkPublicationToPolicy(Node policyLatestHandle, Node publicationHandle) throws RepositoryException {
        String path = policyLatestHandle.getPath();
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        boolean committed = false;
        try {
            Document editable = documentManager.obtainEditableDocument(path);
            Node draft = editable.getCheckedOutNode(session);
            hippoUtils.createMirror(draft, "govscot:relatedItems", publicationHandle);
            // the raw addNode above is only a transient change in our session - commitEditableDocument
            // invokes the workflow, which operates on what is actually persisted, so this must be saved
            // first or the addition is silently lost (matches WorkflowDocumentVariantImportTask's own
            // bind-then-save-then-commit sequence).
            session.save();
            documentManager.commitEditableDocument(path);
            committed = true;
            documentManager.publishDocument(path);
            LOG.debug("linked publication {} to policy {}", publicationHandle.getPath(), path);
        } catch (DocumentManagerException | RepositoryException e) {
            LOG.warn("Unable to link publication {} to policy {}, skipping: {}",
                    publicationHandle.getPath(), path, e.getMessage());
            // release the checked-out draft left behind by a failed obtain/bind, so this policy page
            // doesn't stay locked under the importer's identity
            if (!committed) {
                try {
                    documentManager.disposeEditableDocument(path);
                } catch (DocumentManagerException disposeException) {
                    LOG.warn("Failed to release editable instance for policy {}", path, disposeException);
                }
            }
        }
    }

    private Node findPolicyLatestNode(String policy) throws RepositoryException {
        String sql = "SELECT * FROM govscot:PolicyLatest WHERE jcr:path LIKE " +
                "'/content/documents/govscot/policies/%s/latest/%%' AND hippostd:state = 'published'";
        return hippoUtils.findOne(session, sql, policy);
    }

    private boolean alreadyRelated(Node policyLatest, Node publication) throws RepositoryException {
        NodeIterator nodeIterator = policyLatest.getNodes("govscot:relatedItems");
        while (nodeIterator.hasNext()) {
            Node relatedItem = nodeIterator.nextNode();
            if (relatedItem.getProperty("hippo:docbase").getString().equals(publication.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

}
