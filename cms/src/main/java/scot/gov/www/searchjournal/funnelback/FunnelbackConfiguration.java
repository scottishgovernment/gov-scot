package scot.gov.www.searchjournal.funnelback;

public class FunnelbackConfiguration {

    private String searchType;

    private String apiUrl;

    private String clientId;

    private String apiKey;

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

