package scot.gov.publishing.hippo.sso;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import jakarta.servlet.http.HttpSession;

import java.net.URI;

public class RedirectHandler {

    private static final Scope SCOPE = new Scope("openid", "profile", "email");

    private final OidcConfig oidcConfig;

    public RedirectHandler(OidcConfig oidcConfig) {
        this.oidcConfig = oidcConfig;
    }

    public String buildRedirectUrl(HttpSession session) {
        State state = new State();
        Nonce nonce = new Nonce();
        CodeVerifier codeVerifier = new CodeVerifier();
        session.setAttribute(SsoSessionAttributes.CODE_VERIFIER, codeVerifier);
        session.setAttribute(SsoSessionAttributes.STATE, state);
        session.setAttribute(SsoSessionAttributes.NONCE, nonce);

        AuthenticationRequest authRequest = new AuthenticationRequest.Builder(
                new ResponseType(ResponseType.Value.CODE),
                SCOPE,
                oidcConfig.clientId(),
                URI.create(oidcConfig.redirectUri()))
                .endpointURI(oidcConfig.authorizationEndpoint())
                .codeChallenge(codeVerifier, CodeChallengeMethod.S256)
                .state(state)
                .responseType(ResponseType.CODE)
                .nonce(nonce)
                .build();

        return authRequest.toURI().toString();
    }
}
