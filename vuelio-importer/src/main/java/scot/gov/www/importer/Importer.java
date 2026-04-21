package scot.gov.www.importer;

public enum Importer {

    SCOTGOV("scotgov"),
    SOCIALSECURITY("socialsecurity");

    String name;

    Importer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
