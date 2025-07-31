package demo.hippo.notifications.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.hippoecm.htmldiff.DiffHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.jcr.*;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;


public final class HtmlDiffGenerator {
    private static final Logger log = LoggerFactory.getLogger(HtmlDiffGenerator.class);
    public static final String UTF_8 = "UTF-8";

    /**
     * Produce diff html for given nodes. If there is no published node, no diff is returned ("new document)
     *
     * @param oldNode  oldNode node
     * @param newNode  updated node
     * @param prefixes property/child nodes prefixes for which we are creating diff like {@code "foo:*, bar:*"}
     * @return html diff
     */
    public static String diff(final Node oldNode, final Node newNode, final Locale locale, final String... prefixes) throws RepositoryException, SAXException, IOException, TransformerConfigurationException {
        if (oldNode == null || newNode == null) {
            return "No diff: new document";
        }
        final StringBuilder diffBuilder = new StringBuilder();
        // properties
        final PropertyIterator properties = newNode.getProperties(prefixes);
        while (properties.hasNext()) {
            final Property p = properties.nextProperty();
            String diff = diffProperties(locale, p, oldNode);
            if (StringUtils.isNotBlank(diff) && diff.contains("<span style=\"")) {
                diffBuilder.append("<div><b>" + WordUtils.capitalize(p.getName().replace("demo:", "")) + "</b><br/>" + diff + "<br/><br/></div>");
            }
        }
        //
        final NodeIterator nodes = newNode.getNodes(prefixes);
        while (nodes.hasNext()) {
            final Node node = nodes.nextNode();
            String diff = diffNode(locale, node, oldNode);
            if (StringUtils.isNotBlank(diff) && diff.contains("<span style=\"")) {
                diffBuilder.append("<div><b>").append(WordUtils.capitalize(node.getName().replace("demo:", ""))).append("</b>").append(diff).append("<br/></div>");
            }
        }
        return diffBuilder.toString();
    }

    private static String diffNode(final Locale locale, final Node node, final Node oldNode) throws RepositoryException, SAXException, IOException, TransformerConfigurationException {
        final String name = node.getPrimaryNodeType().getName();
        if (name.equals("hippostd:html")) {
            final Property property = node.getProperty("hippostd:content");
            final String newValue = property.getString();
            final String nodeName = node.getName();
            if (oldNode.hasNode(nodeName)) {
                final Node htmlNode = oldNode.getNode(nodeName);
                final String oldValue = htmlNode.getProperty("hippostd:content").getString();
                return createDiff(locale, oldValue, newValue);
            }

            return createDiff(locale, "", newValue);
        }
        return "";
    }


    private static String diffProperties(final Locale locale, final Property property, final Node oldNode) throws IOException, TransformerConfigurationException, SAXException, RepositoryException {
        return diffString(property, oldNode, locale);

    }

    private static String wrapMultiProperties(final Property property) throws RepositoryException {
        final Value[] values = property.getValues();
        final StringBuilder builder = new StringBuilder();
        for (Value value : values) {
            builder.append(' ');
            builder.append(value.getString());
        }
        return builder.toString();
    }


    private static String diffString(final Property property, final Node oldNode, final Locale locale) throws RepositoryException, SAXException, IOException, TransformerConfigurationException {
        if (property.isMultiple()) {
            //String oldText =  wrapMultiProperties(property);
            // String newText =  wrapMultiProperties(oldNod.getP);
            // return createDiff(locale, oldText, newText);
            return "";
        }
        String oldText = "";
        // check if property exists:
        final String name = property.getName();
        if (oldNode.hasProperty(name)) {
            final Property oldProperty = oldNode.getProperty(name);
            oldText = oldProperty.getValue().getString();
        }
        final String newText = property.getValue().getString();

        return createDiff(locale, oldText, newText);

    }

    private static String createDiff(final Locale locale, final String oldText, final String newText) throws IOException, TransformerConfigurationException, SAXException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DiffHelper.diffHtml(oldText, newText, new StreamResult(baos), locale);
        // doesn't need to be closed
        return baos.toString(UTF_8)
                .replace("<html>", "")
                .replace("</html>", "")
                .replace("class=\"diff-html-added\"", "style=\"background-color: #e2fed9!important;\"")
                .replace("class=\"diff-html-removed\"", "style=\"text-decoration: line-through;background-color: #fdc6c6;\"");
    }

    private HtmlDiffGenerator() {
    }
}
