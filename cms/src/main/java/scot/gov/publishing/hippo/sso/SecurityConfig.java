package scot.gov.publishing.hippo.sso;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml5AuthenticationProvider;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

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
        authenticationProvider.setResponseAuthenticationConverter(new SamlResponseConverter());
        AuthenticationManager authenticationManager = new CmsAuthenticationManager(authenticationProvider);

        return httpSecurity
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(http -> { http.
                    requestMatchers(
                            "/angular/**",
                            "/ckeditor/**",
                            "/favicon.ico",
                            "/navapp-assets/**",
                            "/saml2/**",
                            "/skin/**",
                            "/login**",
                            "/ping/",
                            "/wicket/**",
                            "/ws/indexexport",
                            "/ws/**"
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

}
