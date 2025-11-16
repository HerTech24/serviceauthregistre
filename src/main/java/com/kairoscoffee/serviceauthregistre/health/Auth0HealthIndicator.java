package com.kairoscoffee.serviceauthregistre.health;

import com.kairoscoffee.serviceauthregistre.service.Auth0Service;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class Auth0HealthIndicator implements HealthIndicator {

    private final Auth0Service auth0Service;

    public Auth0HealthIndicator(Auth0Service auth0Service) {
        this.auth0Service = auth0Service;
    }

    @Override
    public Health health() {
        try {
            // Intenta obtener JWKS del issuer
            auth0Service.checkAuth0Connection();
            // Nota: solo hacemos request al JWKS, no necesitamos un token real

            return Health.up().withDetail("auth0", "Conexión correcta con Auth0").build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("auth0", "Error conectando con Auth0")
                    .withException(e)
                    .build();
        }
    }
}
