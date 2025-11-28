package com.kairoscoffee.serviceauthregistre.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType; // normalmente "Bearer"
    private UsuarioDTO user;  // ← AGREGAR ESTO
}