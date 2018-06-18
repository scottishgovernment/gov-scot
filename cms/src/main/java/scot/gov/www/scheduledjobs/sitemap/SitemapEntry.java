package scot.gov.www.scheduledjobs.sitemap;

import java.util.Calendar;

/**
 * Sitemap entry contains a url and the last modification date
 */
public class SitemapEntry {
    private String loc;
    private Calendar lastModified;

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public Calendar getLastModified() {
        return lastModified;
    }

    public void setLastModified(Calendar lastModified) {
        this.lastModified = lastModified;
    }
}
