package scot.gov.www.pressreleases;

import scot.gov.www.pressreleases.prgloo.PRGlooContentType;
import scot.gov.www.pressreleases.prgloo.PRGlooException;
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
    ChangeHistory fetchHistoryCall(Instant from) throws PressReleaseImporterException {
        try {
            return prgloo.changesCorrespondences(from);
        } catch (PRGlooException e) {
            throw new PressReleaseImporterException("failed to fetch history", e);
        }
    }
}