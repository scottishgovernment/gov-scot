package scot.gov.publications.hippo;

import org.onehippo.forge.content.exim.core.DocumentManager;
import scot.gov.publications.metadata.Metadata;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.ZonedDateTime;

/**
 * Created by z418868 on 14/07/2020.
 */
public class DocumentHelper {

    Session session;

    HippoNodeFactory nodeFactory;

    HippoUtils hippoUtils = new HippoUtils();


    public DocumentHelper(Session session) {
        this.session = session;
        this.nodeFactory = new HippoNodeFactory(session);
    }

    public void ensurePublicationState(
            DocumentManager documentManager,
            Node node,
            String path,
            Metadata metadata)
            throws RepositoryException {

        boolean isCurrentlyPublished = hippoUtils.hasPublishedVariant(node);
        boolean passedPublicationTime = metadata.getPublicationDateWithTimezone().isBefore(ZonedDateTime.now());
        if (passedPublicationTime && !isCurrentlyPublished) {
            documentManager.publishDocument(path);
        }

        if (!passedPublicationTime && isCurrentlyPublished) {
            documentManager.depublishDocument(path);
        }

        nodeFactory.ensureEmbargoAndWorkflowJobs(node, metadata.getPublicationDateWithTimezone(), metadata.shoudlEmbargo());
    }
}
