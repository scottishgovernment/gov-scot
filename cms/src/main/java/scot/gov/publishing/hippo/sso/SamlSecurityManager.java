package scot.gov.publishing.hippo.sso;

import org.hippoecm.repository.security.AuthenticationStatus;
import org.hippoecm.repository.security.SecurityManager;
import org.hippoecm.repository.security.SecurityProvider;
import org.hippoecm.repository.security.user.HippoUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.lang.reflect.Field;
import java.util.Map;

public class SamlSecurityManager extends SecurityManager {

    private static final Logger LOG = LoggerFactory.getLogger(SamlSecurityManager.class);


    @Override
    public void configure() throws RepositoryException {
        super.configure();
        SecurityProvider internal = provider("internal");
        SamlSecurityProvider saml = (SamlSecurityProvider) provider("saml");
        saml.setInternalProvider(internal);
    }

    @Override
    public AuthenticationStatus authenticate(SimpleCredentials creds) {
        if (!creds.getUserID().contains("@") || containsPassword(creds)) {
            return super.authenticate(creds);
        }

        String userId = creds.getUserID();
        try {
            SecurityProvider internalProvider = provider("internal");
            HippoUserManager internalUserManager = (HippoUserManager) internalProvider.getUserManager();
            if (!internalUserManager.hasUser(userId)) {
                LOG.debug("New user: {}", userId);
            } else if (!internalUserManager.isActive(userId)) {
                LOG.debug("Inactive user: {}", userId);
                return AuthenticationStatus.ACCOUNT_EXPIRED;
            } else if (internalUserManager.isPasswordExpired(userId)) {
                LOG.debug("Password expired for user: {}", userId);
                return AuthenticationStatus.CREDENTIAL_EXPIRED;
            }

            SecurityProvider samlProvider = provider("saml");
            HippoUserManager userManager = (HippoUserManager) samlProvider.getUserManager();
            boolean authenticated = userManager.authenticate(creds);
            if (!authenticated) {
                return AuthenticationStatus.FAILED;
            }

            samlProvider.synchronizeOnLogin(creds);
            return AuthenticationStatus.SUCCEEDED;
        } catch (RepositoryException ex) {
            return AuthenticationStatus.FAILED;
        }
    }

    /**
     * Return true if the credentials contain a password supplied by the user, and
     * false if the password is empty or a sentinel password set when using SSO.
     */
    private static boolean containsPassword(SimpleCredentials credentials) {
        char[] password = credentials.getPassword();
        return password.length > 1 || password[0] != 0;
    }

    private SecurityProvider provider(String name) {
        try {
            Field field = SecurityManager.class.getDeclaredField("providers");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, SecurityProvider> providers = (Map<String, SecurityProvider>) field.get(this);
            return providers.get(name);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

}
