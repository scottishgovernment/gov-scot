package scot.gov.www.pressreleases.health;

import java.util.HashMap;
import java.util.Map;

public enum Importer {

    PRESS_RELEASES(
            "Press releases",
            "press-release-importer",
            6),
    SPEECHES(
            "Speeches/Briefings",
            "speech-briefing-importer",
            6),
    CORRESPONDENCES(
            "Correspondences",
            "correspondence-importer",
            6),
    TAGS(
            "Tags",
            "tag-importer",
            65);

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
