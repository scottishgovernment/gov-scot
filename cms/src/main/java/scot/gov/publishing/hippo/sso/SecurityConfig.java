package scot.gov.publishing.hippo.sso;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml5AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
@PropertySource("classpath:application.properties")
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${saml.metadata.url}")
    String metadataUrl;

    @Bean
    SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

        OpenSaml5AuthenticationProvider authenticationProvider = new OpenSaml5AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(groupsConverter());
        AuthenticationManager authenticationManager = new CmsAuthenticationManager(authenticationProvider);

        return httpSecurity
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(http -> { http.
                    requestMatchers(
                            "/angular/**",
                            "/skin/**",
                            "/wicket/**",
                            "/saml2/**",
                            "/login**",
                            "*.ico",
                            "*.gif",
                            "*.jpg",
                            "*.jpeg",
                            "*.png",
                            "*.js",
                            "*.css"
                    ).permitAll()
                    .requestMatchers(m -> {
                        return StringUtils.contains(m.getQueryString(), "UserLoggedOut");
                    })
                    .permitAll()
                    .anyRequest()
                    .authenticated();
                })
                .addFilterAfter(new PostAuthorisationFilter(), AuthorizationFilter.class)
                .saml2Login(saml2 -> {
                    saml2.authenticationManager(authenticationManager);
                })
                .saml2Logout(withDefaults())
                .build();
    }

    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrations() {
        log.info("Using {}", metadataUrl);
        RelyingPartyRegistration registration = RelyingPartyRegistrations
                .fromMetadataLocation(metadataUrl)
                .registrationId("auth0")
                .build();
        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }

    private Converter<OpenSaml5AuthenticationProvider.ResponseToken, Saml2Authentication> groupsConverter() {

        Converter<OpenSaml5AuthenticationProvider.ResponseToken, Saml2Authentication> delegate =
                new OpenSaml5AuthenticationProvider.ResponseAuthenticationConverter();

        return (responseToken) -> {
            Saml2Authentication authentication = delegate.convert(responseToken);
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
            List<String> groups = principal.getAttribute("http://schemas.xmlsoap.org/claims/Group");
            Set<GrantedAuthority> authorities = new HashSet<>();
            if (groups != null) {
                groups.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
            } else {
                authorities.addAll(authentication.getAuthorities());
            }
            return new Saml2Authentication(principal, authentication.getSaml2Response(), authorities);
        };
    }
}
