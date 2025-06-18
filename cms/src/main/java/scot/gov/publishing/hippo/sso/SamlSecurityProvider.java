package scot.gov.publishing.hippo.sso;

import org.apache.jackrabbit.api.security.user.UserManager;
import org.hippoecm.repository.security.DelegatingSecurityProvider;
import org.hippoecm.repository.security.RepositorySecurityProvider;
import org.hippoecm.repository.security.user.HippoUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

/**
 * Custom <code>org.hippoecm.repository.security.SecurityProvider</code> implementation.
 * <p>
 * Hippo Repository allows to set a custom security provider for various reasons (e.g, SSO) for specific users.
 * If a user is associated with a custom security provider, then Hippo Repository invokes
 * the custom security provider to do authentication and authorization.
 * </P>
 */
public class SamlSecurityProvider extends DelegatingSecurityProvider {

    private static final Logger log = LoggerFactory.getLogger(SamlSecurityProvider.class);

    private HippoUserManager userManager;

    /**
     * Constructs by creating the default <code>RepositorySecurityProvider</code> to delegate all the other calls
     * except of authentication calls.
     *
     * @throws RepositoryException
     */
    public SamlSecurityProvider() throws RepositoryException {
        super(new RepositorySecurityProvider());
    }

    /**
     * Returns a custom (delegating) HippoUserManager to authenticate a user by SAML Assertion.
     */
    @Override
    public UserManager getUserManager() throws RepositoryException {
        if (userManager == null) {
            HippoUserManager hippoUserManager = (HippoUserManager) super.getUserManager();
            userManager = new SamlUserManager(hippoUserManager, getGroupManager());
        }
        return userManager;
    }

    /**
     * Returns a custom (delegating) HippoUserManager to authenticate a user by SAML Assertion.
     */
    @Override
    public UserManager getUserManager(Session session) throws RepositoryException {
        HippoUserManager hippoUserManager = (HippoUserManager) super.getUserManager(session);
        return new SamlUserManager(hippoUserManager, getGroupManager());
    }

    @Override
    public void synchronizeOnLogin(SimpleCredentials creds) throws RepositoryException {
        super.synchronizeOnLogin(creds);
    }

}
