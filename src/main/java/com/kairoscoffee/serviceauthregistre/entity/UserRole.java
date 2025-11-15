package com.kairoscoffee.serviceauthregistre.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_ROLES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ID_ROL", nullable = false)
    private Rol rol;
}
