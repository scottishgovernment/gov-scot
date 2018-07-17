package scot.gov.www.scheduledjobs.sitemap;

import java.util.Calendar;

/**
 * Created by z418868 on 17/07/2018.
 */
public class UrlAndDateModified {

    private final String url;
    private final Calendar dateModified;

    public UrlAndDateModified(String url, Calendar dateModified) {
        this.url = url;
        this.dateModified = dateModified;
    }

    public String getUrl() {
        return url;
    }

    public Calendar getDateModified() {
        return dateModified;
    }
}
