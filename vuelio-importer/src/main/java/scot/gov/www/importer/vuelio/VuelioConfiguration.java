package scot.gov.www.importer.vuelio;

import scot.gov.www.importer.Importer;

public class VuelioConfiguration {

    Importer importer;

    private String api;

    private String token;

    public Importer getImporter() {
        return importer;
    }

    public void setImporter(Importer importer) {
        this.importer = importer;
    }

    public String getApi() { return api; }

    public void setApi(String api) { this.api = api; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

}
