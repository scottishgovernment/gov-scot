package scot.gov.publishing.hippo.sso;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.Nonce;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CallbackHandler}.
 *
 * <p>These tests cover the error-handling paths that do not require a live OIDC
 * provider. The {@code configured} flag is set to {@code true} via reflection so
 * that {@code ensureConfigured()} is a no-op (avoiding HST infrastructure).
 */
public class CallbackHandlerTest {

    private CallbackHandler sut;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;

    @Before
    public void setUp() throws Exception {
        sut = new CallbackHandler();

        // Bypass lazy OIDC configuration so tests run without HST/Hippo infrastructure.
        Field configuredField = CallbackHandler.class.getDeclaredField("configured");
        configuredField.setAccessible(true);
        configuredField.set(sut, true);

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(req.getSession()).thenReturn(session);
        when(req.getSession(anyBoolean())).thenReturn(session);
        when(req.getContextPath()).thenReturn("/cms");
        when(session.getAttribute(SsoSessionAttributes.RETURN_URL)).thenReturn("/cms/");
    }

    // -- IdP error --

    @Test
    public void idpErrorStoresSsoErrorAndRedirects() throws Exception {
        when(req.getParameter("error")).thenReturn("access_denied");
        when(req.getParameter("error_description")).thenReturn("User cancelled");

        sut.handleRequest(req, resp);

        verify(session).setAttribute(SsoSessionAttributes.SSO_ERROR, "access_denied");
        verify(resp).sendRedirect("/cms/");
    }

    @Test
    public void idpErrorRedirectsToReturnUrl() throws Exception {
        when(req.getParameter("error")).thenReturn("login_required");
        when(req.getParameter("error_description")).thenReturn(null);
        when(session.getAttribute(SsoSessionAttributes.RETURN_URL)).thenReturn("/console/");

        sut.handleRequest(req, resp);

        verify(resp).sendRedirect("/console/");
    }

    @Test
    public void idpErrorFallsBackToContextRootWhenReturnUrlMissing() throws Exception {
        when(req.getParameter("error")).thenReturn("access_denied");
        when(req.getParameter("error_description")).thenReturn(null);
        when(session.getAttribute(SsoSessionAttributes.RETURN_URL)).thenReturn(null);

        sut.handleRequest(req, resp);

        verify(resp).sendRedirect("/cms/");
    }

    // -- Missing callback parameters --

    @Test
    public void missingStateParameterSetsCallbackErrorAndRedirects() throws Exception {
        when(req.getParameter("error")).thenReturn(null);
        when(req.getParameter("state")).thenReturn(null);
        when(req.getParameter("code")).thenReturn("some-auth-code");

        sut.handleRequest(req, resp);

        verify(session).setAttribute(SsoSessionAttributes.CALLBACK_ERROR, true);
        verify(resp).sendRedirect("/cms/");
    }

    @Test
    public void missingCodeParameterSetsCallbackErrorAndRedirects() throws Exception {
        when(req.getParameter("error")).thenReturn(null);
        when(req.getParameter("state")).thenReturn("some-state");
        when(req.getParameter("code")).thenReturn(null);
        when(session.getAttribute(SsoSessionAttributes.STATE)).thenReturn(new State("some-state"));
        when(session.getAttribute(SsoSessionAttributes.CODE_VERIFIER)).thenReturn(new CodeVerifier());
        when(session.getAttribute(SsoSessionAttributes.NONCE)).thenReturn(new Nonce());

        sut.handleRequest(req, resp);

        verify(session).setAttribute(SsoSessionAttributes.CALLBACK_ERROR, true);
        verify(resp).sendRedirect("/cms/");
    }

    // -- State mismatch --

    @Test
    public void stateMismatchSetsCallbackErrorAndRedirects() throws Exception {
        when(req.getParameter("error")).thenReturn(null);
        when(req.getParameter("state")).thenReturn("attacker-state");
        when(req.getParameter("code")).thenReturn("some-auth-code");
        when(session.getAttribute(SsoSessionAttributes.STATE)).thenReturn(new State("expected-state"));
        when(session.getAttribute(SsoSessionAttributes.CODE_VERIFIER)).thenReturn(new CodeVerifier());
        when(session.getAttribute(SsoSessionAttributes.NONCE)).thenReturn(new Nonce());

        sut.handleRequest(req, resp);

        verify(session).setAttribute(SsoSessionAttributes.CALLBACK_ERROR, true);
        verify(resp).sendRedirect("/cms/");
    }

    // -- Session expiry (missing OIDC session attributes) --

    @Test
    public void missingSessionStateSetsCallbackErrorAndRedirects() throws Exception {
        when(req.getParameter("error")).thenReturn(null);
        when(req.getParameter("state")).thenReturn("some-state");
        when(req.getParameter("code")).thenReturn("some-auth-code");
        // STATE not in session — simulates a session that expired between the redirect and callback
        when(session.getAttribute(SsoSessionAttributes.STATE)).thenReturn(null);
        when(session.getAttribute(SsoSessionAttributes.CODE_VERIFIER)).thenReturn(new CodeVerifier());
        when(session.getAttribute(SsoSessionAttributes.NONCE)).thenReturn(new Nonce());

        sut.handleRequest(req, resp);

        verify(session).setAttribute(SsoSessionAttributes.CALLBACK_ERROR, true);
        verify(resp).sendRedirect("/cms/");
    }

    // -- OIDC attribute cleanup on error --

    @Test
    public void callbackErrorClearsOidcSessionAttributes() throws Exception {
        when(req.getParameter("error")).thenReturn(null);
        when(req.getParameter("state")).thenReturn(null);
        when(req.getParameter("code")).thenReturn("some-auth-code");

        sut.handleRequest(req, resp);

        verify(session).removeAttribute(SsoSessionAttributes.STATE);
        verify(session).removeAttribute(SsoSessionAttributes.NONCE);
        verify(session).removeAttribute(SsoSessionAttributes.CODE_VERIFIER);
    }

}
