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
import java.util.Map;
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
    private final Auth0Service auth0Service;   // ⚡ Importante

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

        String accessToken =
                jwtService.generateToken(usuario.getEmail(), usuario.getRol().getNombre());
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

        String accessToken =
                jwtService.generateToken(usuario.getEmail(), usuario.getRol().getNombre());
        String refreshToken = createRefreshToken(usuario);

        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }

    // ============================================================
    // 3. LOGIN SOCIAL (AUTH0) — id_token
    // ============================================================
    public AuthResponse loginWithAuth0(String idToken) {

        // 1) Validar el id_token con JWKs de Auth0
        Map<String, Object> claims = auth0Service.validateIdToken(idToken);

        // 2) Extraer datos necesarios
        String email = (String) claims.get("email");
        String sub = (String) claims.get("sub");
        String name = (String) claims.get("name");

        String provider = "AUTH0";

        // 3) Buscar si ya existe el AuthProvider
        var opt = authProviderRepository.findByProviderAndProviderUserId(provider, sub);
        Usuario usuario;

        if (opt.isPresent()) {
            usuario = opt.get().getUsuario();
        } else {
            // Buscar por email
            var byEmail = usuarioRepository.findByEmail(email);

            if (byEmail.isPresent()) {
                usuario = byEmail.get();
            } else {
                // Crear usuario nuevo
                String nombre = (name != null && name.contains(" "))
                        ? name.split(" ")[0]
                        : (name != null ? name : "Auth0");

                String apellido = (name != null && name.contains(" "))
                        ? name.substring(name.indexOf(' ') + 1)
                        : "";

                usuario = userService.createUserLocal(
                        nombre,
                        apellido,
                        email,
                        null,
                        UUID.randomUUID().toString() // contraseña temporal
                );
            }

            // Crear enlace AuthProvider
            AuthProvider ap = AuthProvider.builder()
                    .provider(provider)
                    .providerUserId(sub)
                    .usuario(usuario)
                    .build();

            authProviderRepository.save(ap);
        }

        // 4) Generar tokens
        String accessToken =
                jwtService.generateToken(usuario.getEmail(), usuario.getRol().getNombre());
        String refreshToken = createRefreshToken(usuario);

        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }

    // ============================================================
    // 4. CREAR REFRESH TOKEN
    // ============================================================
    private String createRefreshToken(Usuario usuario) {

        String token = UUID.randomUUID().toString();

        RefreshToken refresh = RefreshToken.builder()
                .token(token)
                .expiration(LocalDateTime.now().plusDays(7))
                .usuario(usuario)
                .build();

        refreshTokenRepository.save(refresh);

        return token;
    }

    // ============================================================
    // 5. REFRESCAR ACCESS TOKEN
    // ============================================================
    public AuthResponse refreshAccessToken(String refreshToken) {

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh Token no válido."));

        if (stored.getExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh Token expirado.");
        }

        Usuario usuario = stored.getUsuario();

        String newAccessToken =
                jwtService.generateToken(usuario.getEmail(), usuario.getRol().getNombre());

        return new AuthResponse(newAccessToken, refreshToken, "Bearer");
    }

    // ============================================================
    // 6. LOGOUT → elimina refresh token
    // ============================================================
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
            refreshTokenRepository.delete(rt);
        });
    }
}
