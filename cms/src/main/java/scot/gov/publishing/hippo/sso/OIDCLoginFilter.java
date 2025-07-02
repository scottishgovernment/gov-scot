package scot.gov.publishing.hippo.sso;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hippoecm.frontend.model.UserCredentials;

import java.io.IOException;
import java.net.URI;

public class OIDCLoginFilter implements Filter {

    private static final String CLIENT_ID = "";
    private static final String REDIRECT_URI = "https://lcl.publishing.gov.scot/callback";
    private static final String AUTHORIZATION_ENDPOINT = "https://cloudhothouse.okta.com/oauth2/default/v1/authorize";
    private static final String ISSUER = "https://cloudhothouse.okta.com";
    private static final Scope SCOPE = new Scope("openid", "profile", "email");

    public static final String CREDENTIALS_ATTR_NAME = UserCredentials.class.getName();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("id_token") != null) {
            System.out.println("Already authed " + req.getServletPath());
            Object creds = session.getAttribute(CREDENTIALS_ATTR_NAME);
            req.setAttribute(CREDENTIALS_ATTR_NAME, creds);
            chain.doFilter(request, response); // Already logged in
            return;
        }

        // Redirect to OIDC provider
        State state = new State();
        Nonce nonce = new Nonce();
        session = req.getSession(true);
        session.setAttribute("state", state);
        session.setAttribute("nonce", nonce);

        AuthenticationRequest authRequest = new AuthenticationRequest.Builder(
                new ResponseType(ResponseType.Value.CODE),
                SCOPE,
                new ClientID(CLIENT_ID),
                URI.create(REDIRECT_URI))
                .endpointURI(URI.create(AUTHORIZATION_ENDPOINT))
                .state(state)
                .responseType(ResponseType.CODE)
                .nonce(nonce)
                .build();

        String url = authRequest.toURI().toString();
        System.out.println(url);
        res.sendRedirect(url);
    }
}
