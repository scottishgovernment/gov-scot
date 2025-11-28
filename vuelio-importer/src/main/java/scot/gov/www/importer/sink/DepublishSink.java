package scot.gov.www.importer.sink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.importer.domain.PressRelease;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class DepublishSink extends AbstractSink {

    private static final Logger LOG = LoggerFactory.getLogger(DepublishSink.class);

    Session session;

    public DepublishSink(Session session) {
        this.session = session;
    }

    @Override
    public void acceptPressRelease(PressRelease pressRelease) throws RepositoryException {
        //Not implemented
    }

    @Override
    public void removeDeletedPressRelease(String id) throws RepositoryException {
        String found = depublish(id, session);
        if (found==null) {
            LOG.info("No matching document ID found to depublish: {}", id);
        }
    }
}
