package scot.gov.www.pressreleases;

import retrofit2.Call;
import scot.gov.www.pressreleases.prgloo.PRGlooContentType;
import scot.gov.www.pressreleases.prgloo.rest.ChangeHistory;
import scot.gov.www.pressreleases.sink.PressReleaseSink;
import scot.gov.www.pressreleases.sink.PublicationSink;

import javax.jcr.Session;
import java.time.Instant;

public class SpeechImporter extends AbstractImporter {

    public SpeechImporter(Session session) {
        super(session);
    }

    @Override
    String importerName() {
        return "speech-briefing-importer";
    }

    @Override
    PRGlooContentType prGlooContentType() {
        return PRGlooContentType.SPEECH_BRIEFING;
    }

    @Override
    PressReleaseSink sink() {
        return new PublicationSink(session, "govscot:Publication", "speech-statement","new-speech-or-statement-folder");
    }

    @Override
    Call<ChangeHistory> fetchHistoryCall(Instant from) {
        return prgloo.changesSpeeches(from);
    }
}
