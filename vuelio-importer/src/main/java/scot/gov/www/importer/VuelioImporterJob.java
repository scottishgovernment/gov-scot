package scot.gov.www.importer;

import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.importer.vuelio.VuelioClient;
import scot.gov.www.importer.vuelio.VuelioConfiguration;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

/**
 * Scheduled job to import press releases (news, speeches and correspondence).
 */
public class VuelioImporterJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(VuelioImporterJob.class);
    //
    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        // if no vuelio token is configured, do not run the job
        VuelioConfiguration vuelioConfiguration = VuelioClient.config();
        if (vuelioConfiguration == null) {
            LOG.info("No Vuelio token configured, not running");
            return;
        }

        Session systemSession = context.createSystemSession();
        Session session = null;
        try {
            Credentials credentials = new SimpleCredentials("news", "".toCharArray());
            session = systemSession.impersonate(credentials);
            doImport(session);
        } catch (RepositoryException | VuelioImporterException e) {
            LOG.error("failed ", e);
        } finally {
            if (session != null) {
                session.logout();
            }
            systemSession.logout();

        }
    }

    void doImport(Session session) throws RepositoryException {
        LOG.info("ContentImporter running");
        new VuelioImporter(session).doImport();
        LOG.info("ContentImporter finished");
    }

}