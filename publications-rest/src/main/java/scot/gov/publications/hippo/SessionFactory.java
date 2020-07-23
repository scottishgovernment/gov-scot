package scot.gov.publications.hippo;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

public interface SessionFactory {

    /**
     * Create a new JCR session.
     *
     * @return new JCR session
     * @throws RepositoryException if session could not be created
     */
    Session newSession() throws RepositoryException;
}
