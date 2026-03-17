package scot.gov.www.importer.health;

import java.util.HashMap;
import java.util.Map;

public enum Importer {

    VULEO_SCOTGOV("Vuelio Importer scotgov","vuelio-importer",6),
    VULEO_SOCIAL_SECURITY("Vuelio Importer Social Security","vuelio-importer-social-security",6);

    static Map<String, Importer> byId = new HashMap<>();

    String name;

    String node;

    int minutesThreshold;

    static {
        for (Importer importer : Importer.values()) {
            byId.put(importer.node, importer);
        }
    }

    Importer(String name, String node, int minutesThreshold) {
        this.name = name;
        this.node = node;
        this.minutesThreshold = minutesThreshold;

    }

    public static Importer forId(String id) {
        return byId.get(id);
    }

}
