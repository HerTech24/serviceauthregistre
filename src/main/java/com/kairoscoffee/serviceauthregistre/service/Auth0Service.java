package com.kairoscoffee.serviceauthregistre.service;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Auth0Service {

    @Value("${auth0.issuer}")
    private String issuer;

    @Value("${auth0.jwks-uri}")
    private String jwksUri;

    @Value("${auth0.audience:}")
    private String audience;

    private JWKSource<SecurityContext> jwkSource;

    private synchronized JWKSource<SecurityContext> getJwkSource() throws Exception {
        if (jwkSource == null) {
            jwkSource = new RemoteJWKSet<>(new URL(jwksUri));
        }
        return jwkSource;
    }

    /**
     * Valida id_token recibido de Auth0 y devuelve claims (map).
     * Lanza RuntimeException si el token no es válido.
     */
    public Map<String, Object> validateIdToken(String idToken) {
        try {
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWKSource<SecurityContext> jwkSrc = getJwkSource();
            JWSKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSrc);
            jwtProcessor.setJWSKeySelector(keySelector);

            // procesa y verifica firma/exp
            JWTClaimsSet claims = jwtProcessor.process(idToken, null);

            // issuer
            if (!issuer.equals(claims.getIssuer())) {
                throw new RuntimeException("Issuer inválido");
            }

            // audience (opcional)
            if (audience != null && !audience.isBlank() && !claims.getAudience().contains(audience)) {
                throw new RuntimeException("Audience inválida");
            }

            return claims.getClaims();
        } catch (Exception e) {
            throw new RuntimeException("id_token inválido de Auth0: " + e.getMessage(), e);
        }
    }

    public void checkAuth0Connection() throws Exception {
        getJwkSource(); // intenta crear RemoteJWKSet con jwksUri
    }
}
