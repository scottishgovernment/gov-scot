package scot.gov.pressreleases;

public enum Importer {

    PRESS_RELEASES("press-release-importer", 6),
    SPEECHES("speech-briefing-importer", 6),
    CORRESPONDENCES("correspondence-importer", 6),
    TAGS("tag-importer", 65);

    int minutesThreshold;

    String name;

    Importer(String name, int minutesThreshold) {
        this.name = name;
        this.minutesThreshold = minutesThreshold;
    }

}
