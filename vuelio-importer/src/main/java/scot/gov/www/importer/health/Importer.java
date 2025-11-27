package scot.gov.www.importer.health;

import java.util.HashMap;
import java.util.Map;

public enum Importer {

    CONTENT_ITEMS(
            "Vuelio Importer",
            "vuelio-importer",
            6),;

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
