package scot.gov.publishing.hippo.sso;

import org.hippoecm.repository.security.AuthenticationStatus;
import org.hippoecm.repository.security.SecurityManager;
import org.hippoecm.repository.security.SecurityProvider;
import org.hippoecm.repository.security.user.HippoUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import java.lang.reflect.Field;
import java.util.Map;

public class SsoSecurityManager extends SecurityManager {

    private static final Logger LOG = LoggerFactory.getLogger(SsoSecurityManager.class);

    public static final String SSO_PROVIDER_ID = "sso";
    public static final String INTERNAL_PROVIDER_ID = "internal";

    @Override
    public void configure() throws RepositoryException {
        super.configure();
        SecurityProvider internal = provider(INTERNAL_PROVIDER_ID);
        SsoSecurityProvider sso = (SsoSecurityProvider) provider(SSO_PROVIDER_ID);
        sso.setInternalProvider(internal);
    }

    @Override
    public AuthenticationStatus authenticate(SimpleCredentials creds) {
        if (!creds.getUserID().contains("@") || containsPassword(creds)) {
            return super.authenticate(creds);
        }

        String userId = creds.getUserID();
        try {
            SecurityProvider internalProvider = provider(INTERNAL_PROVIDER_ID);
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

            SecurityProvider ssoProvider = provider(SSO_PROVIDER_ID);
            HippoUserManager userManager = (HippoUserManager) ssoProvider.getUserManager();
            boolean authenticated = userManager.authenticate(creds);
            if (!authenticated) {
                return AuthenticationStatus.FAILED;
            }

            ssoProvider.synchronizeOnLogin(creds);
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
        // True if NOT the sentinel password set by the SSO callback.
        return !(password.length == 1 && password[0] == 0);
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
