package scot.gov.publications.hippo.rewriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

/**
 * Used by PublicationLinkRewriter to convert href's in links to facet style links.
 */
class LinkRewriter {

    private static final Logger LOG = LoggerFactory.getLogger(LinkRewriter.class);

    private static final String[] EMPTY = new String[0];

    public static final String CONTENT_ATTRIB  = "hippostd:content";

    /**
     * Rewrite a link to a node to use a facet.
     *
     * @param pageNode The node for this page.
     * @param from The href to be rewritten
     * @param to The node to link to
     */
    void rewriteLinkToFacet(Node pageNode, String from, Node to) throws RepositoryException {
        // determine if we already link to this
        Node contentNode = pageNode.getNode("govscot:content");
        Node facet = ensueFacetSelect(contentNode, to);
        String fromhtml = contentNode.getProperty(CONTENT_ATTRIB).getString();
        String toHtml = fromhtml.replaceAll(
                quoted(from),
                quoted(facet.getName()));
        contentNode.setProperty(CONTENT_ATTRIB, toHtml);

        LOG.info("Rewriting {} -> {} in page {}, created new facet for {}",
                from,
                facet.getName(),
                pageNode.getPath(),
                to.getPath());
    }

    String quoted(String str) {
        return String.format("\"%s\"", str);
    }

    void rewriteWithoutFacet(Node pageNode, String from, String to) throws RepositoryException  {
        Node contentNode = pageNode.getNode("govscot:content");
        String fromhtml = contentNode.getProperty(CONTENT_ATTRIB).getString();
        String toHtml = fromhtml.replaceAll(from, to);
        contentNode.setProperty(CONTENT_ATTRIB, toHtml);
        LOG.info("Rewriting {} -> {} in page (no facet)", from, to, pageNode.getPath());
    }

    Node ensueFacetSelect(Node contentNode, Node to) throws RepositoryException {
        NodeIterator it = contentNode.getNodes();
        while (it.hasNext()) {
            Node node = it.nextNode();
            if ("hippo:facetselect".equals(node.getPrimaryNodeType().getName())) {
                String docbase = node.getProperty("hippo:docbase").getString();
                if (docbase.equals(to.getIdentifier())) {
                    return node;
                }
            }
        }

        // no facet exists for this node
        Node facet = contentNode.addNode(to.getIdentifier(), "hippo:facetselect");
        facet.setProperty("hippo:docbase", to.getIdentifier());
        facet.setProperty("hippo:facets", EMPTY);
        facet.setProperty("hippo:modes", EMPTY);
        facet.setProperty("hippo:values", EMPTY);
        return facet;
    }

}
