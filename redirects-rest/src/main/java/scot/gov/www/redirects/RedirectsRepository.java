package scot.gov.www.redirects;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

import static java.util.Arrays.asList;
import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.substringAfter;

public class RedirectsRepository {

    private static final String NT_UNSTRUCTURED = "nt:unstructured";

    private static final String REDIRECTS = "redirects";

    private static final String ALIASES = "Aliases";

    private static final String URL = "govscot:url";

    private static final String DESCRIPTION = "govscot:description";

    private static final String ALIASES_ROOT = "/content/redirects/" + ALIASES;

    Session session;

    public RedirectsRepository(Session session) {
        this.session = session;
    }

    public void createRedirects(Collection<Redirect> redirects) throws RepositoryException {
        for (Redirect redirect : redirects) {
            doCreateRedirect(redirect);
        }
        session.save();
    }

    public RedirectResult list(String path) throws RepositoryException {
        Node node = getNode(path);
        if (node == null) {
            return null;
        }
        NodeIterator it = node.getNodes();
        List<Redirect> children = new ArrayList<>();
        while (it.hasNext()) {
            Node child = it.nextNode();
            children.add(redirect(child));
        }

        RedirectResult redirectResult = new RedirectResult();
        redirectResult.setChildren(children);
        populateRedirect(redirectResult, node);
        return redirectResult;
    }

    Node getNode(String path) throws RepositoryException {
        Node root = aliasesRoot();
        if ("/".equals(path)) {
            return root;
        }

        if (!root.hasNode(path)) {
            return null;
        }
        return root.getNode(path);
    }

    public boolean deleteRedirect(String path) throws RepositoryException {
        Node root = aliasesRoot();
        if (!root.hasNode(path)) {
            return false;
        }

        Node node = root.getNode(path);

        // there is no URL at this node so return false to indicate no action was taken
        if (!node.hasProperty(URL)) {
            return false;
        }

        if (!node.getNodes().hasNext()) {
            node.remove();
        } else {
            node.getProperty(URL).remove();
            node.setProperty(DESCRIPTION, "");
        }
        session.save();
        return true;
    }

    Redirect redirect(Node node) throws RepositoryException {
        Redirect redirect = new Redirect();
        populateRedirect(redirect, node);
        return redirect;
    }

    void populateRedirect(Redirect redirect, Node node) throws RepositoryException {
        String path = node.getPath();
        redirect.setFrom(substringAfter(path, ALIASES_ROOT));
        redirect.setTo(node.hasProperty(URL) ? node.getProperty(URL).getString() : "");
        redirect.setDescription(node.hasProperty(DESCRIPTION) ? node.getProperty(DESCRIPTION).getString() : "");
    }

    void doCreateRedirect(Redirect redirect) throws RepositoryException {
        Node node = ensureRedirectPath(redirect.getFrom());
        node.setProperty(URL, redirect.getTo());
        node.setProperty(DESCRIPTION, description(redirect));
    }

    String description(Redirect redirect) {
        return isBlank(redirect.getTo())
                ? "created by rest api with no description"
                : redirect.getDescription();
    }
    Node ensureRedirectPath(String from) throws RepositoryException {
        return ensureRedirectPath(
                aliasesRoot(),
                0,
                asList(removeLeadingSlash(from).split("/")));
    }

    Node aliasesRoot() throws RepositoryException {
        Node content = session.getNode("/content");
        Node redirectRoot = content.hasNode(REDIRECTS)
                ? content.getNode(REDIRECTS) : content.addNode(REDIRECTS, NT_UNSTRUCTURED);
        return redirectRoot.hasNode(ALIASES)
                ? redirectRoot.getNode(ALIASES) : redirectRoot.addNode(ALIASES, NT_UNSTRUCTURED);
    }

    String removeLeadingSlash(String path) {
        return path.startsWith("/") ? substringAfter(path, "/") : path;
    }

    private Node ensureRedirectPath(Node parent, int pos, List<String> path) throws RepositoryException {
        if (pos == path.size()) {
            return parent;
        }
        String element = path.get(pos);
        Node next = parent.hasNode(element)
                ? parent.getNode(element)
                : parent.addNode(element, NT_UNSTRUCTURED);
        return ensureRedirectPath(next, pos + 1, path);
    }
}
