package scot.gov.publications.hippo;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.InputStream;

public class BinarySource {

    Session session;

    public BinarySource(Session session) {
        this.session = session;
    }

    public Binary get(InputStream in) throws RepositoryException {
        return session.getValueFactory().createBinary(in);
    }

}
