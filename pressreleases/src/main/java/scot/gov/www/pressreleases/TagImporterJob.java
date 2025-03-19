package scot.gov.www.pressreleases;

import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.pressreleases.prgloo.PRGlooClient;
import scot.gov.www.pressreleases.prgloo.PRGlooConfiguration;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

public class TagImporterJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(TagImporterJob.class);
    //
    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        // if no prgloo token is configured, do not run the job
        PRGlooConfiguration prGlooConfiguration = PRGlooClient.config();
        if (prGlooConfiguration == null) {
            LOG.info("No PRGloo token configured, not running");
            return;
        }


        Session systemSession = context.createSystemSession();
        Session session = null;
        try {
            Credentials credentials = new SimpleCredentials("news", "".toCharArray());
            session = systemSession.impersonate(credentials);
            doImport(session);
        } catch (RepositoryException | PressReleaseImporterException e) {
            LOG.error("failed ", e);
        } finally {
            if (session != null) {
                session.logout();
            }
            systemSession.logout();

        }
    }

    void doImport(Session session) throws RepositoryException {
        LOG.info("TagImporter running");
        new TagImporter(session).doImport();
        LOG.info("TagImporter finished");
    }

}