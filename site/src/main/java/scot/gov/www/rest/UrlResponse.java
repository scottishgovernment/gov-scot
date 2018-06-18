package scot.gov.www.rest;

import java.util.Map;

/**
 * Created by z418868 on 23/06/2018.
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
