package scot.gov.publishing.hippo.sso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CmsAuthenticationManager extends ProviderManager {

    private static final Logger log = LoggerFactory.getLogger(CmsAuthenticationManager.class);

    public CmsAuthenticationManager(AuthenticationProvider authenticationProvider) {
        super(authenticationProvider);
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        Authentication authentication = super.authenticate(auth);
        log.info("authentication {} {}", authentication.getName(), authentication);
        return authentication;
    }

}
