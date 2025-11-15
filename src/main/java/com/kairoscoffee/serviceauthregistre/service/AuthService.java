package com.kairoscoffee.serviceauthregistre.service;

import com.kairoscoffee.serviceauthregistre.dto.AuthResponse;
import com.kairoscoffee.serviceauthregistre.entity.AuthProvider;
import com.kairoscoffee.serviceauthregistre.entity.RefreshToken;
import com.kairoscoffee.serviceauthregistre.entity.Usuario;
import com.kairoscoffee.serviceauthregistre.repository.AuthProviderRepository;
import com.kairoscoffee.serviceauthregistre.repository.RefreshTokenRepository;
import com.kairoscoffee.serviceauthregistre.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthProviderRepository authProviderRepository;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    // ============================================================
    // 1. REGISTRO LOCAL
    // ============================================================
    public AuthResponse register(String nombre,
                                 String apellido,
                                 String email,
                                 String telefono,
                                 String password) {

        if (userService.existsByEmail(email)) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        Usuario usuario = userService.createUserLocal(
                nombre, apellido, email, telefono, password
        );

        AuthProvider provider = AuthProvider.builder()
                .provider("LOCAL")
                .providerUserId(email)
                .usuario(usuario)
                .build();

        authProviderRepository.save(provider);

        String accessToken = jwtService.generateToken(usuario.getEmail(), usuario.getRol().getNombre());
        String refreshToken = createRefreshToken(usuario);

        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }

    // ============================================================
    // 2. LOGIN LOCAL
    // ============================================================
    public AuthResponse login(String email, String password) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        String accessToken = jwtService.generateToken(usuario.getEmail(), usuario.getRol().getNombre());
        String refreshToken = createRefreshToken(usuario);

        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }

    // ============================================================
    // 3. GENERAR Y GUARDAR REFRESH TOKEN
    // ============================================================
    private String createRefreshToken(Usuario usuario) {

        String token = UUID.randomUUID().toString();

        RefreshToken refresh = RefreshToken.builder()
                .token(token)
                .expiration(LocalDateTime.now().plusDays(7)) // 7 días
                .usuario(usuario)
                .build();

        refreshTokenRepository.save(refresh);

        return token;
    }

    // ============================================================
    // 4. REFRESCAR ACCESS TOKEN
    // ============================================================
    public AuthResponse refreshAccessToken(String refreshToken) {

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh Token no válido."));

        if (stored.getExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh Token expirado.");
        }

        Usuario usuario = stored.getUsuario();

        String accessToken = jwtService.generateToken(usuario.getEmail(), usuario.getRol().getNombre());

        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }
}
