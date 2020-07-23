package scot.gov.publications.hippo.rewriter;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static scot.gov.publications.hippo.rewriter.LinkRewriter.CONTENT_ATTRIB;

/**
 * Rewrite links in the content of a page node such that
 * makes the link portable - if the linked to item is moved within hippo then the link will automatically update.
 *
 * Links that this rewrites look like this:
 * <a href="SCT01188871401-00.pdf">pdf format</a>
 *
 * The map passed into this class contains a map of filenames to the publication nodes for those attachments.
 */
public class PublicationLinkRewriter {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationLinkRewriter.class);

    LinkRewriter linkRewriter = new LinkRewriter();

    String publicationSlug;

    Map<String, Node> pageNodesByEntryname;

    public PublicationLinkRewriter(String publicationSlug, Map<String, Node> pageNodesByEntryname) {
        this.publicationSlug = publicationSlug;
        this.pageNodesByEntryname = pageNodesByEntryname;
    }

    public void rewrite(Node publicationFolder) throws RepositoryException {
        NodeIterator pageIterator = publicationFolder.getNode("pages").getNodes();
        while (pageIterator.hasNext()) {
            Node pageHandle = pageIterator.nextNode();
            Node page = pageHandle.getNodes().nextNode();
            rewritePage(page);
        }
    }

    private void rewritePage(Node pageNode) throws RepositoryException {
        Node htmlNode = pageNode.getNode("govscot:content");
        String html = htmlNode.getProperty(CONTENT_ATTRIB).getString();
        Document htmlDoc = Jsoup.parse(html);
        List<Element> links = htmlDoc.select("a[href]")
                .stream().filter(this::isLocalLink)
                .collect(toList());
        for (Element link : links) {
            rewriteLink(link.attr("href"), pageNode);
        }
    }

    private void rewriteLink(String href, Node pageNode) throws RepositoryException {

        LOG.info("rewriteLink {} -> {}", href, pageNode.getPath());

        // if the href is one of the pages then rewrite it as a facet link
        if (pageNodesByEntryname.containsKey(href)) {
            Node pagenode = pageNodesByEntryname.get(href);
            Node toNode = pagenode.isNodeType("hippo:resource") ? pagenode : pagenode.getParent();
            linkRewriter.rewriteLinkToFacet(pageNode, href, toNode);
            return;
        }

        // see if this is a link to a page with an anchor
        String hrefWithoutAnchor = StringUtils.substringBefore(href, "#");
        if (pageNodesByEntryname.containsKey(hrefWithoutAnchor)) {
            String pageSlug = pageNodesByEntryname.get(hrefWithoutAnchor).getName();
            String anchor = substringAfter(href, "#");
            String newLink = String.format("/publications/%s/pages/%s#%s",
                    publicationSlug,
                    pageSlug,
                    anchor);
            linkRewriter.rewriteWithoutFacet(pageNode, href, newLink);
            return;
        }
    }

    private boolean isLocalLink(Element link) {
        String href = link.attr("href");
        if (StringUtils.isEmpty(href)) {
            return false;
        }

        // what about mailtos ftp etc?
        return !href.startsWith("http");
    }
}
