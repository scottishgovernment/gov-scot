package scot.gov.www;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.equalsAny;

public class UrlSource {

    public String url(Node node) throws RepositoryException {

        if (node.hasProperty("govscot:slug")) {
            return slugUrl(node);
        }

        if (equalsAny(node.getName(), "index", "statistics-and-research")) {
            return urlForNode(node.getParent().getParent());
        }

        return urlForNode(node.getParent());
    }

    String slugUrl(Node node) throws RepositoryException {
        String type = node.getPath().split("/")[4];
        String slug = node.getProperty("govscot:slug").getString();
        return new StringBuilder("/")
                .append(type)
                .append('/')
                .append(slug)
                .append("/")
                .toString();
    }


    String urlForNode(Node node) throws RepositoryException {
        String path = node.getPath();
        path = substringAfter(path, "/content/documents/" + sitename(node));

        Set<String> fixedPaths = new HashSet<>();
        Collections.addAll(fixedPaths, "topics", "siteitems");
        String firstElement = path.split("/")[1];

        if (fixedPaths.contains(firstElement)) {
            path = path.substring(firstElement.length() + 1);
        }
        return path + "/";
    }

    public static String sitename(Node node) throws RepositoryException {
        return node.getPath().split("/")[3];
    }
}
