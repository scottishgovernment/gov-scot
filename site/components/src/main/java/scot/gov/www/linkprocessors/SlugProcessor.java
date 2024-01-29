package scot.gov.www.linkprocessors;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Created by z441571 on 21/04/2020.
 */
public abstract class SlugProcessor extends HstLinkProcessorTemplate {

    protected boolean isFullLink(HstLink link, String typePath, int pathLength) {
        // should match on any link.
        return link.getPath().startsWith(typePath) && link.getPathElements().length >= pathLength;
    }

    protected Node findNode(String path) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();

        return session.nodeExists(path) ?
                findPublishedNodeFromFolder(session, path)
                : null;
    }

    protected Node findPublishedNodeFromFolder(Session session, String path) throws RepositoryException {
        Node folder = session.getNode(path);
        Node handle = handleFromFolder(folder);
        return findPublishedNode(handle.getNodes(handle.getName()));
    }

    private Node handleFromFolder(Node folder) throws RepositoryException {
        if (folder.isNodeType("hippo:handle")) {
            return folder;
        }

        NodeIterator it = folder.getNodes();
        while (it.hasNext()) {
            Node candidate = it.nextNode();
            if (candidate.isNodeType("hippo:handle")) {
                return candidate;
            }
        }
        return null;
    }

    protected Node findPublishedNode(NodeIterator nodeIterator) throws RepositoryException {

        Node publishedNode = null;
        Node lastNode = null;
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            lastNode = node;
            if (node.isNodeType("govscot:basedocument") && "published".equals(node.getProperty("hippostd:state").getString())) {
                publishedNode = node;
            }
        }

        return publishedNode != null ? publishedNode : lastNode;
    }

    protected boolean isSlugLink(HstLink link, String typePath, int pathLength) {
        return link.getPath().startsWith(typePath) && link.getPathElements().length == pathLength;
    }

}
