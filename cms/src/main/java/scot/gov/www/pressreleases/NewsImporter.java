package scot.gov.www.pressreleases;

import scot.gov.www.pressreleases.prgloo.PRGlooContentType;
import scot.gov.www.pressreleases.prgloo.PRGlooException;
import scot.gov.www.pressreleases.prgloo.rest.ChangeHistory;
import scot.gov.www.pressreleases.sink.NewsSink;
import scot.gov.www.pressreleases.sink.PressReleaseSink;

import javax.jcr.Session;
import java.time.Instant;

public class NewsImporter extends AbstractImporter {

    public NewsImporter(Session session) {
        super(session);
    }

    @Override
    String importerName() {
        return "press-release-importer";
    }

    @Override
    PRGlooContentType prGlooContentType() {
        return PRGlooContentType.PRESS_RELEASE;
    }

    @Override
    PressReleaseSink sink() {
        return new NewsSink(session);
    }

    @Override
    ChangeHistory fetchHistoryCall(Instant from) {
        try {
            return prgloo.changesNews(from);
        } catch (PRGlooException e) {
            throw new PressReleaseImporterException("failed to fetch history", e);
        }
    }
}
