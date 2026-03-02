package scot.gov.publishing.hippo.sso;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.apache.commons.lang3.ObjectUtils;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.function.Supplier;

public record OidcConfig(
        String issuer,
        URI authorizationEndpoint,
        URI jwksUri,
        URI tokenEndpoint,
        URI userInfoEndpoint,
        ClientID clientId,
        Supplier<ClientAuthentication> clientAuthentication,
        String redirectUri,
        JWKSet publicJwks
) {

    private static final Logger LOG = LoggerFactory.getLogger(OidcConfig.class);

    static OidcConfig get() {
        try {
            return loadConfig();
        } catch (IOException | GeneralException | JOSEException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static OidcConfig loadConfig() throws GeneralException, IOException, JOSEException {
        ContainerConfiguration config = HstServices.getComponentManager().getContainerConfiguration();

        String issuer = config.getString("oidc.issuer");
        String authorizationEndpoint = config.getString("oidc.authorization.endpoint");
        String jwksUri = config.getString("oidc.jwks.uri");
        String tokenEndpoint = config.getString("oidc.token.endpoint");
        String userInfoEndpoint = config.getString("oidc.userinfo.endpoint");

        if (ObjectUtils.anyNull(authorizationEndpoint, jwksUri, tokenEndpoint, userInfoEndpoint)) {
            OIDCProviderMetadata metadata = OIDCProviderMetadata.resolve(new Issuer(issuer));
            if (authorizationEndpoint == null) {
                authorizationEndpoint = metadata.getAuthorizationEndpointURI().toString();
            }
            if (jwksUri == null) {
                jwksUri = metadata.getJWKSetURI().toString();
            }
            if (tokenEndpoint == null) {
                tokenEndpoint = metadata.getTokenEndpointURI().toString();
            }
            if (userInfoEndpoint == null) {
                userInfoEndpoint = metadata.getUserInfoEndpointURI().toString();
            }
        }

        ClientID clientId = new ClientID(config.getString("oidc.client.id"));
        URI tokenEndpointUri = URI.create(tokenEndpoint);

        String keyFile = config.getString("oidc.client.key.file");
        Supplier<ClientAuthentication> clientAuthentication;
        JWKSet publicJwks;
        if (keyFile != null) {
            // PrivateKeyJWT signs a JWT at construction time, so create a fresh one per request.
            RSAKey rsaKey = loadRsaKey(keyFile);
            PrivateKey privateKey = rsaKey.toRSAPrivateKey();
            String keyID = rsaKey.getKeyID();
            clientAuthentication = () -> createClientAuthentication(
                    clientId, tokenEndpointUri, privateKey, keyID);
            publicJwks = new JWKSet(rsaKey.toPublicJWK());
        } else {
            // ClientSecretBasic is stateless and can be reused.
            Secret clientSecret = new Secret(config.getString("oidc.client.secret"));
            clientAuthentication = () -> new ClientSecretBasic(clientId, clientSecret);
            publicJwks = new JWKSet(Collections.emptyList());
        }

        return new OidcConfig(
                issuer,
                URI.create(authorizationEndpoint),
                URI.create(jwksUri),
                tokenEndpointUri,
                URI.create(userInfoEndpoint),
                clientId,
                clientAuthentication,
                config.getString("oidc.redirect.uri"),
                publicJwks
        );
    }

    private static RSAKey loadRsaKey(String keyFile) throws IOException, JOSEException {
        String pem = Files.readString(Path.of(keyFile));
        JWK jwk = JWK.parseFromPEMEncodedObjects(pem);
        RSAKey rsaKey = new RSAKey.Builder(jwk.toRSAKey())
                .keyID(jwk.computeThumbprint().toString())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();
        LOG.info("Using private key JWT client authentication.");
        LOG.info("Public key JWK for IdP configuration: {}", rsaKey.toPublicJWK().toJSONString());
        return rsaKey;
    }

    private static ClientAuthentication createClientAuthentication(
            ClientID clientId, URI tokenEndpoint, PrivateKey privateKey, String keyID) {
        try {
            return new PrivateKeyJWT(
                    clientId, tokenEndpoint, JWSAlgorithm.RS256,
                    privateKey, keyID, null, null, null);
        } catch (JOSEException ex) {
            throw new RuntimeException(ex);
        }
    }

}
