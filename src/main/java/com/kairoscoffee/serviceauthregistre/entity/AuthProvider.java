package com.kairoscoffee.serviceauthregistre.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AUTH_PROVIDERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROVIDER_ID")
    private Long id;

    @Column(name = "PROVIDER", nullable = false)
    private String provider;  // "GOOGLE", "AUTH0"

    @Column(name = "PROVIDER_USER_ID", nullable = false)
    private String providerUserId; // ID real del usuario en Google/Auth0

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;
}
