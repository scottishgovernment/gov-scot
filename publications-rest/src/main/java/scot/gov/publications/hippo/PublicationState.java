package scot.gov.publications.hippo;

/**
 * The embargo/scheduling/publish state of a document after {@link DocumentHelper#ensurePublicationState}
 * has reconciled it.
 */
public class PublicationState {

    private final boolean embargoed;

    private final boolean scheduled;

    private final boolean published;

    public PublicationState(boolean embargoed, boolean scheduled, boolean published) {
        this.embargoed = embargoed;
        this.scheduled = scheduled;
        this.published = published;
    }

    public boolean isEmbargoed() {
        return embargoed;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public boolean isPublished() {
        return published;
    }
}
