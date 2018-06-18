package scot.gov.www.scheduledjobs.sitemap;

import java.util.Collection;

/**
 * Request from the urls rest web service
 */
public class UrlRequest {
    private Collection<String> paths;

    public Collection<String> getPaths() {
        return paths;
    }

    public void setPaths(Collection<String> paths) {
        this.paths = paths;
    }
}
