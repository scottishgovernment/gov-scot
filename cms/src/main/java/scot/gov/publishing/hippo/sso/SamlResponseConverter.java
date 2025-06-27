package scot.gov.publishing.hippo.sso;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml5AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml5AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Converts a SAML2 response token into a Saml2Authentication instance.
 */
public class SamlResponseConverter implements Converter<ResponseToken, Saml2Authentication> {

    public static final String GROUP_ATTR = "http://schemas.auth0.com/groups";

    private final Converter<ResponseToken, Saml2Authentication> delegate =
            new OpenSaml5AuthenticationProvider.ResponseAuthenticationConverter();

    @Override
    public Saml2Authentication convert(ResponseToken responseToken) {
        Saml2Authentication authentication = delegate.convert(responseToken);
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();

        List<String> groups = principal.getAttribute(GROUP_ATTR);
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (groups != null) {
            for (String group : groups) {
                authorities.add(new SimpleGrantedAuthority(group));
            }
        } else {
            authorities.addAll(authentication.getAuthorities());
        }
        return new Saml2Authentication(principal, authentication.getSaml2Response(), authorities);
    }

}
