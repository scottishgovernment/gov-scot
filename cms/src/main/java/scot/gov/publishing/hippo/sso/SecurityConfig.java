package scot.gov.publishing.hippo.sso;

import jakarta.servlet.http.HttpSession;
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
//                .logout(logout -> logout.logoutSuccessHandler((request, response, authentication) -> {
//                    String url = "https://login.microsoftonline.com/" + System.getenv("SSO_TENANT_ID") + "/oauth2/v2.0/logout";
//                    request.setAttribute("logoutUrl", url.toString());
//                    request.getRequestDispatcher("/WEB-INF/logout-redirect.jsp").forward(request, response);
//                    response.flushBuffer();
//                }))
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(http -> { http.
                    requestMatchers(
                            "/angular/**",
                            "/ckeditor/**",
                            "/favicon.ico",
                            "/internal",
                            "/navapp-assets/**",
                            "/saml2/**",
                            "/skin/**",
                            "/login**",
                            "/ping/",
                            "/sso",
                            "/wicket/**",
                            "/ws/navigationitems",
                            "/ws/indexexport",
                            "/ws/redirects"
                    ).permitAll()
                    .requestMatchers(m -> {
                        boolean logout = "loginmessage=UserLoggedOut".equals(m.getQueryString())
                                || "0&loginmessage=UserLoggedOut".equals(m.getQueryString())
                                || "1-1.0-root-activeLogout&iframe".equals(m.getQueryString())
                                || "0-1.-root-login~panel-login~form".equals(m.getQueryString());
                        HttpSession session = m.getSession(false);
                        if (session != null && logout) {
                            session.setAttribute("sso", false);
                        }
                        return logout;
                    }, m -> {
                        HttpSession session = m.getSession(false);
                        boolean allow = false;
                        if (session != null) {
                            allow = session.getAttribute("sso") != null;
                        }
                        log.info("request: {} session: {} sso: {}", m.getRequestURI(), session != null ? session.getId() : null, allow);
                        return allow;
                    }
                    )
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
