package scot.gov.www.importer.vuelio;

public class VuelioConfiguration {

//    private String api = "https://webpublish.vuelio.co.uk/api/Proxy/json";
    private String api = "https://webpublishqa.vuelio.co.uk/api/Proxy/json";

    private String token;

    public String getApi() { return api; }

    public void setApi(String api) { this.api = api; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

}
