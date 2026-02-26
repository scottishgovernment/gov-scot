package scot.gov.publishing.hippo.sso;

import org.hippoecm.repository.security.AuthenticationStatus;
import org.hippoecm.repository.security.SecurityManager;
import org.hippoecm.repository.security.SecurityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * SecurityManager implementation that uses the SSO provider for authentication,
 * regardless of the value of the hipposys:securityprovider property for that user.
 * This allows the use of SSO for any user, regardless of whether it is configured
 * with the sso provider.
 */
public class SsoSecurityManager extends SecurityManager {

    private static final Logger LOG = LoggerFactory.getLogger(SsoSecurityManager.class);

    public static final String SSO_PROVIDER_ID = "sso";

    @Override
    public AuthenticationStatus authenticate(SimpleCredentials creds) {
        String userId = creds.getUserID();
        try {
            SecurityProvider ssoProvider = provider(SSO_PROVIDER_ID);
            SsoUserManager ssoUserManager = (SsoUserManager) ssoProvider.getUserManager();

            String repositoryUserId = ssoUserManager.resolveUserId(userId);
            if (repositoryUserId != null) {
                userId = repositoryUserId;
                if (!ssoUserManager.isActive(userId)) {
                    LOG.debug("Inactive user: {}", userId);
                    return AuthenticationStatus.ACCOUNT_EXPIRED;
                }

                boolean isSsoLogin = creds.getAttribute(SsoAttributes.SSO_ID) != null;
                if (!isSsoLogin && ssoUserManager.isPasswordExpired(userId)) {
                    LOG.debug("Password expired for user: {}", userId);
                    return AuthenticationStatus.CREDENTIAL_EXPIRED;
                }
            }

            if (!ssoUserManager.authenticate(creds)) {
                return AuthenticationStatus.FAILED;
            }

            creds.setAttribute("providerId", SSO_PROVIDER_ID);
            ssoProvider.synchronizeOnLogin(creds);
            return AuthenticationStatus.SUCCEEDED;
        } catch (RepositoryException ex) {
            LOG.warn("Error authenticating user: {}", userId, ex);
            return AuthenticationStatus.FAILED;
        }
    }

    private SecurityProvider provider(String name) {
        // The SecurityManager base class holds providers in a private "providers" map
        // and does not expose it through any public or protected API. Reflection is the
        // only option short of copying the whole SecurityManager class.
        // If this approach is not feasible in the future, then the solution would be to
        // add `securityprovider: sso` to users, and ensure that passwords could not
        // expire. It may also be necessary to ensure usernames are lowercase to match
        // user IDs in the IdP.
        try {
            Field field = SecurityManager.class.getDeclaredField("providers");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, SecurityProvider> providers = (Map<String, SecurityProvider>) field.get(this);
            return providers.get(name);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException("Incompatible change to Bloomreach SecurityManager", ex);
        }
    }

}
