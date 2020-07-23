package scot.gov.publications.hippo.pages;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.ApsZipImporterException;

import java.util.List;

/**
 * Utility methods used to update publication pages.
 */
public class HtmlUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlUtil.class);

    /**
     * Determine the title of a publication page.  If the page contains any h3 elements then the first one will be used
     * as its title. Otherwise the index will be used to format a generic title e.g. "Page 1"
     *
     * In older publications an h3 was used as the page heading.  However we have requested that they start to use
     * h2.  For this reason this code has to be able to handle either.
     */
    public String getTitle(Element div, int index) {

        String title = getTitle(div, "h2");

        if (title != null) {
            return title;
        }

        title = getTitle(div, "h3");

        if (title != null) {
            return title;
        }

        LOG.warn("Page does not contain an h2 or h3, will use page number to format page title {}", index);
        return String.format("Page %d", index);
    }

    public String getTitle(Element div, String tag) {
        List<Element> headings = div.select(tag);
        return headings.isEmpty() ? null : headings.get(0).text();
    }

    /**
     * Extract the contents of the .mainText div.
     *
     * @throws ApsZipImporterException If the document does not contains exactly one mainText div.
     */
    public Element getMainText(Document htmlDoc) throws ApsZipImporterException {
        List<Element> elements = htmlDoc.select(".mainText");
        if (elements.size() != 1) {
            throw new ApsZipImporterException("Page does not contain a single .mainText div");
        }
        return elements.get(0);
    }

    /**
     * Determine if theis is a contents page.  Contents pages are ones whose first h3 element is either
     * "Content" or "Table of contents" (case insensitive)
     */
    public boolean isContentsPage(String html) {
        if (StringUtils.isEmpty(html)) {
            return false;
        }

        Document htmlDoc = Jsoup.parse(html);
        String title = getTitle(htmlDoc, 0).toLowerCase();
        return StringUtils.equalsAny(title, "contents", "table of contents");

    }

}
