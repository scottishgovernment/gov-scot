package scot.gov.www.searchjournal.funnelback;

public enum FunnelbackCollection {
    NEWS("govscot~ds-news-push"),
    POLICY("govscot~ds-policies-push"),
    PUBLICATIONS("govscot~ds-publications-push"),
    PUBLICATIONS_OTHER("govscot~ds-publications-other-push"),
    STATS_AND_RESEARCH("govscot~ds-statistics-research-push"),
    JOURNAL("govscot~ds-journal-push");

    private final String collectionName;

    FunnelbackCollection(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }
}