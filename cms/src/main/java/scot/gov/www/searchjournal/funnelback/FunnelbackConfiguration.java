package scot.gov.www.searchjournal.funnelback;

public class FunnelbackConfiguration {

    private String apiUrl;

    private String apiKey;

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPushCollectionApiPath() {
        return new StringBuilder(apiUrl)
                .append("push-api/v2/collections/")
                .toString();
    }

}

