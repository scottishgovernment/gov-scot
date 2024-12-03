package scot.gov.www.pressreleases;

import retrofit2.Call;
import scot.gov.www.pressreleases.prgloo.PRGlooContentType;
import scot.gov.www.pressreleases.prgloo.rest.ChangeHistory;
import scot.gov.www.pressreleases.sink.PressReleaseSink;
import scot.gov.www.pressreleases.sink.PublicationSink;

import javax.jcr.Session;
import java.time.Instant;

public class CorrespondenceImporter extends AbstractImporter {

    public CorrespondenceImporter(Session session) {
        super(session);
    }

    @Override
    String importerName() {
        return "correspondence-importer";
    }

    @Override
    PRGlooContentType prGlooContentType() {
        return PRGlooContentType.CORRESPONDENCE;
    }

    @Override
    PressReleaseSink sink() {
        return new PublicationSink(session, "govscot:Publication", "correspondence","new-publication-folder");
    }

    @Override
    Call<ChangeHistory> fetchHistoryCall(Instant from) {
        return prgloo.changesCorrespondences(from);
    }
}