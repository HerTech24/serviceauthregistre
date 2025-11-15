package com.kairoscoffee.serviceauthregistre.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String password;
}
