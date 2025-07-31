package demo.hippo.notifications.util;

import demo.hippo.notifications.NotificationsEventsListener;
import org.hippoecm.repository.HippoStdNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.NoSuchElementException;

public class HippoUtils {

    private static final Logger log = LoggerFactory.getLogger(NotificationsEventsListener.class);

    public static Node getPublishedVariant(Node handleNode) throws RepositoryException {
        for (NodeIterator variantsIterator = handleNode.getNodes(handleNode.getName()); variantsIterator.hasNext(); ) {
            Node documentVariant = variantsIterator.nextNode();
            String state = documentVariant.getProperty(HippoStdNodeType.HIPPOSTD_STATE).getString();
            if (HippoStdNodeType.PUBLISHED.equals(state)) {
                return documentVariant;
            }
        }
        return null;
    }

    public static Node getUnpublishedVariant(Node handleNode) throws RepositoryException {
        for (NodeIterator variantsIterator = handleNode.getNodes(handleNode.getName()); variantsIterator.hasNext(); ) {
            Node documentVariant = variantsIterator.nextNode();
            String state = documentVariant.getProperty(HippoStdNodeType.HIPPOSTD_STATE).getString();
            if (HippoStdNodeType.UNPUBLISHED.equals(state)) {
                return documentVariant;
            }
        }
        return null;
    }

    public static Node getSourceDocumentHandle(Node translatedVariantHandleNode) {
        try {
            Node documentVariantNode = translatedVariantHandleNode.isNodeType("hippo:handle") ?
                    translatedVariantHandleNode.getNode(translatedVariantHandleNode.getName()) :
                    translatedVariantHandleNode;

            if (!documentVariantNode.hasProperty("hippotranslation:id")) {
                return null;
            }
            String sourceDocumentTranslationId = documentVariantNode.getProperty("hippotranslation:id").getString();
            try {
                NodeIterator it = documentVariantNode.getSession().getWorkspace().getQueryManager()
                        .createQuery("//element(*, " + documentVariantNode.getPrimaryNodeType().getName() + ")[@hippotranslation:id='" + sourceDocumentTranslationId + "']", Query.XPATH)
                        .execute().getNodes();

                while (it.hasNext()) {
                    Node parentHandle = it.nextNode().getParent();
                    if (parentHandle.isNodeType("translationsaddon:translatable")) {
                        return parentHandle;
                    }
                }

                return null;

            } catch (NoSuchElementException e) {
                return null;
            }
        } catch (RepositoryException e) {
            log.error("Repository exception while trying to find translated variant", e);
            return null;
        }
    }
}
