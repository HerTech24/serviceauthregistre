package com.kairoscoffee.serviceauthregistre.controller;

import com.kairoscoffee.serviceauthregistre.dto.*;
import com.kairoscoffee.serviceauthregistre.entity.Usuario;
import com.kairoscoffee.serviceauthregistre.service.AuthService;
import com.kairoscoffee.serviceauthregistre.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    // ============== REGISTER ==============
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(
                request.getNombre(),
                request.getApellido(),
                request.getEmail(),
                request.getTelefono(),
                request.getPassword()
        );

        return ResponseEntity.ok(response);
    }

    // ============== LOGIN ==============
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        AuthResponse response = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(response);
    }

    // ============== REFRESH TOKEN ==============
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

        AuthResponse response = authService.refreshAccessToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(response);
    }

    // ============== PROFILE ==============
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {

        if (auth == null)
            return ResponseEntity.status(401).body("No autenticado");

        Usuario usuario = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        return ResponseEntity.ok(usuario);
    }
}
