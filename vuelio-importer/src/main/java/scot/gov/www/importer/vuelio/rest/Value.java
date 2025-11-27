package scot.gov.www.importer.vuelio.rest;

public enum Value {

    NEWS_RELEASE("News release"),
    SPEECH("Speech/Statement"),
    CORRESPONDENCE("Correspondence");

    private String description;

    private Value(String description) { this.description = description; }

    public String getDescription() { return description; }

}
