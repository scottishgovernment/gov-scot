package scot.gov.www.scheduledjobs.sitemap;

import java.util.Map;

/**
 * Response from the url rest service
 */
public class UrlResponse {
    private Map<String, String> urls;

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }

    public Map<String, String> getUrls() {
        return urls;
    }
}
