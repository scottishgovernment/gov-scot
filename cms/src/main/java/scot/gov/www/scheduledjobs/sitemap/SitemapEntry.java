package scot.gov.www.scheduledjobs.sitemap;

import java.util.Calendar;

/**
 * Sitemap entry contains a url and the last modification date
 */
public class SitemapEntry {
    private String loc;
    private String nodeType;
    private Calendar lastModified;

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Calendar getLastModified() {
        return lastModified;
    }

    public void setLastModified(Calendar lastModified) {
        this.lastModified = lastModified;
    }
}
