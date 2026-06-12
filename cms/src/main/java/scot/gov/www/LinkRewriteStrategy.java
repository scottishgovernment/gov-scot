package scot.gov.www;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Strategy for resolving a non-Bloomreach link href to the JCR handle node it points to.
 *
 * <p>Implementations handle the two forms of legacy link found in migrated content:
 * absolute {@code https://www.gov.scot/*} URLs and site-relative paths.
 */
public interface LinkRewriteStrategy {

    /**
     * Resolves {@code href} to a JCR {@code hippo:handle} node.
     *
     * @param session active JCR session
     * @param href    the link href from the HTML content — either an absolute
     *                {@code https://www.gov.scot/…} URL or a relative path
     * @return the {@code hippo:handle} node to link to, or {@code null} if the href
     *         cannot be resolved to a node in the repository
     * @throws RepositoryException if the JCR lookup fails
     */
    Node findTargetNode(Session session, String href, String htmlPath) throws RepositoryException;
}
