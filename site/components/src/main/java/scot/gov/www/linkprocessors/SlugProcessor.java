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

    private static final String HIPPOSTD_STATE = "hippostd:state";

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

    // when generating a link for the preview mount, the slug must be read from the draft variant
    // (the variant the CMS user's own session sees immediately after an edit, before it is committed
    // to the unpublished variant) so the generated link matches the slug the user just typed.
    protected Node findNode(String path, String mountType) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();

        if (!session.nodeExists(path)) {
            return null;
        }

        Node folder = session.getNode(path);
        Node handle = handleFromFolder(folder);
        if (handle == null) {
            return null;
        }

        if ("preview".equals(mountType)) {
            Node draft = findVariantByState(handle, "draft");
            if (draft != null) {
                return draft;
            }
        }

        return findPublishedNode(handle.getNodes(handle.getName()));
    }

    protected Node findVariantByState(Node handle, String state) throws RepositoryException {
        NodeIterator variants = handle.getNodes(handle.getName());
        while (variants.hasNext()) {
            Node variant = variants.nextNode();
            if (variant.hasProperty(HIPPOSTD_STATE) && state.equals(variant.getProperty(HIPPOSTD_STATE).getString())) {
                return variant;
            }
        }
        return null;
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
            if (node.isNodeType("govscot:basedocument") && "published".equals(node.getProperty(HIPPOSTD_STATE).getString())) {
                publishedNode = node;
            }
        }

        return publishedNode != null ? publishedNode : lastNode;
    }

    protected boolean isSlugLink(HstLink link, String typePath, int pathLength) {
        return link.getPath().startsWith(typePath) && link.getPathElements().length == pathLength;
    }

}
