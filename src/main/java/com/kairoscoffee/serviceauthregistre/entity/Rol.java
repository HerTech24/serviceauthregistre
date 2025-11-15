package com.kairoscoffee.serviceauthregistre.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ROL")
@Data
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ROL")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, unique = true)
    private String nombre;
}