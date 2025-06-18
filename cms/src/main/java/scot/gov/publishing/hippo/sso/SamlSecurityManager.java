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

public class SamlSecurityManager extends SecurityManager {

    private static final Logger LOG = LoggerFactory.getLogger(SamlSecurityManager.class);

    @Override
    public AuthenticationStatus authenticate(SimpleCredentials creds) {
        if (!creds.getUserID().contains("@")) {
            return super.authenticate(creds);
        }

        String userId = creds.getUserID();
        try {
            SecurityProvider internalProvider = provider("internal");
            HippoUserManager internalUserManager = (HippoUserManager) internalProvider.getUserManager();
            if (!internalUserManager.isActive(userId)) {
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
