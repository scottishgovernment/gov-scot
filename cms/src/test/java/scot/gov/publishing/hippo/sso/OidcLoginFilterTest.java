package scot.gov.publishing.hippo.sso;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OidcLoginFilter} — specifically the {@code requireSsoSession} logic
 * and surrounding filter behaviour.
 *
 * <p>{@code configured} is set to {@code true} in {@code setUp()} so that
 * {@code ensureConfigured()} is a no-op; {@code ssoConfig} and (where needed) {@code oidcConfig}
 * are assigned directly, avoiding any HST/Hippo infrastructure.
 */
public class OidcLoginFilterTest {

    private OidcLoginFilter sut;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;
    private FilterChain chain;

    @Before
    public void setUp() {
        sut = new OidcLoginFilter();
        sut.configured = true;
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        chain = mock(FilterChain.class);

        // Defaults: GET request with a non-excluded URI and no context-path prefix.
        when(req.getMethod()).thenReturn("GET");
        when(req.getContextPath()).thenReturn("");
        when(req.getRequestURI()).thenReturn("/cms/");
        when(req.getQueryString()).thenReturn(null);
    }

    /**
     * Minimal {@link OidcConfig} suitable for redirect-path tests; the only URI that matters
     * for assertions is {@code authorizationEndpoint}.
     */
    private OidcConfig testOidcConfig() {
        return new OidcConfig(
                "https://idp.example.com",
                URI.create("https://idp.example.com/auth"),
                URI.create("https://idp.example.com/jwks"),
                URI.create("https://idp.example.com/token"),
                URI.create("https://idp.example.com/userinfo"),
                new ClientID("test-client"),
                () -> new ClientSecretBasic(new ClientID("test-client"), new Secret("secret")),
                "https://cms.example.com/cms/sso/callback",
                new JWKSet()
        );
    }

    // =========================================================================
    // Pass-through cases — filter must call chain.doFilter() and must NOT
    // call response.sendRedirect().
    // =========================================================================

    @Test
    public void offModePassesThrough() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.OFF, SsoConfig.Default.OFF);

        sut.doFilter(req, resp, chain);

        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    @Test
    public void postRequestPassesThrough() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.REQUIRED, SsoConfig.Default.ON);
        when(req.getMethod()).thenReturn("POST");

        sut.doFilter(req, resp, chain);

        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    @Test
    public void excludedPrefixSkinPassesThrough() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.REQUIRED, SsoConfig.Default.ON);
        when(req.getRequestURI()).thenReturn("/skin/logo.png");

        sut.doFilter(req, resp, chain);

        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    @Test
    public void excludedPrefixSsoCallbackPassesThrough() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.REQUIRED, SsoConfig.Default.ON);
        when(req.getRequestURI()).thenReturn("/sso/callback");

        sut.doFilter(req, resp, chain);

        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    @Test
    public void excludedExactPathNavigationItemsPassesThrough() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.REQUIRED, SsoConfig.Default.ON);
        when(req.getRequestURI()).thenReturn("/ws/navigationitems");

        sut.doFilter(req, resp, chain);

        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    @Test
    public void loggedOutSessionPassesThrough() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.REQUIRED, SsoConfig.Default.ON);
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute(SsoSessionAttributes.LOGGED_OUT)).thenReturn(true);

        sut.doFilter(req, resp, chain);

        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    @Test
    public void optionalModeWithSsoCookieFalsePassesThrough() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.OPTIONAL, SsoConfig.Default.OFF);
        when(req.getCookies()).thenReturn(new Cookie[]{new Cookie(SsoFilter.SSO_COOKIE_NAME, "false")});

        sut.doFilter(req, resp, chain);

        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    @Test
    public void optionalModeNoCookieDefaultOffPassesThrough() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.OPTIONAL, SsoConfig.Default.OFF);
        when(req.getCookies()).thenReturn(null);

        sut.doFilter(req, resp, chain);

        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    @Test
    public void credentialsInSessionPassesThroughAndSetsRequestAttribute() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.REQUIRED, SsoConfig.Default.ON);
        Object mockCreds = mock(Object.class);
        when(req.getSession(false)).thenReturn(session);
        when(req.getSession(true)).thenReturn(session);
        when(session.getAttribute(OidcLoginFilter.CREDENTIALS_ATTR_NAME)).thenReturn(mockCreds);

        sut.doFilter(req, resp, chain);

        verify(req).setAttribute(OidcLoginFilter.CREDENTIALS_ATTR_NAME, mockCreds);
        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    // =========================================================================
    // IdP redirect cases — filter must call response.sendRedirect() to the IdP
    // and store STATE, NONCE, and CODE_VERIFIER in the session.
    // =========================================================================

    @Test
    public void requiredModeRedirectsToIdP() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.REQUIRED, SsoConfig.Default.ON);
        sut.redirectHandler = new RedirectHandler(testOidcConfig());
        when(req.getSession(true)).thenReturn(session);

        sut.doFilter(req, resp, chain);

        verify(resp).sendRedirect(argThat((String url) -> url.startsWith("https://idp.example.com/auth")));
        verify(session).setAttribute(eq(SsoSessionAttributes.STATE), any());
        verify(session).setAttribute(eq(SsoSessionAttributes.NONCE), any());
        verify(session).setAttribute(eq(SsoSessionAttributes.CODE_VERIFIER), any());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void optionalModeWithSsoSessionAttrRedirectsToIdP() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.OPTIONAL, SsoConfig.Default.OFF);
        sut.redirectHandler = new RedirectHandler(testOidcConfig());
        when(req.getSession(false)).thenReturn(session);
        when(req.getSession(true)).thenReturn(session);
        when(session.getAttribute(SsoSessionAttributes.SSO)).thenReturn("true");

        sut.doFilter(req, resp, chain);

        verify(resp).sendRedirect(argThat((String url) -> url.startsWith("https://idp.example.com/auth")));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void optionalModeWithSsoCookieTrueRedirectsToIdP() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.OPTIONAL, SsoConfig.Default.OFF);
        sut.redirectHandler = new RedirectHandler(testOidcConfig());
        when(req.getCookies()).thenReturn(new Cookie[]{new Cookie(SsoFilter.SSO_COOKIE_NAME, "true")});
        when(req.getSession(true)).thenReturn(session);

        sut.doFilter(req, resp, chain);

        verify(resp).sendRedirect(argThat((String url) -> url.startsWith("https://idp.example.com/auth")));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void optionalModeNoCookieDefaultOnRedirectsToIdP() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.OPTIONAL, SsoConfig.Default.ON);
        sut.redirectHandler = new RedirectHandler(testOidcConfig());
        when(req.getCookies()).thenReturn(null);
        when(req.getSession(true)).thenReturn(session);

        sut.doFilter(req, resp, chain);

        verify(resp).sendRedirect(argThat((String url) -> url.startsWith("https://idp.example.com/auth")));
        verify(chain, never()).doFilter(any(), any());
    }

    // =========================================================================
    // Spot-checks: redirect URL shape
    // =========================================================================

    @Test
    public void redirectUrlContainsResponseTypeCodeAndStateParam() throws Exception {
        sut.ssoConfig = new SsoConfig(SsoConfig.Mode.REQUIRED, SsoConfig.Default.ON);
        sut.redirectHandler = new RedirectHandler(testOidcConfig());
        when(req.getSession(true)).thenReturn(session);

        sut.doFilter(req, resp, chain);

        verify(resp).sendRedirect(argThat((String url) ->
                url.contains("response_type=code") && url.contains("state=")));
    }
}
