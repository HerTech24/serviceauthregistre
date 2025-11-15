package com.kairoscoffee.serviceauthregistre.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "KairosCoffee AuthService API",
                version = "1.0",
                description = "Microservicio de autenticación y registro (JWT, Refresh Tokens, Auth0)"
        )
)
public class OpenApiConfig {
}
