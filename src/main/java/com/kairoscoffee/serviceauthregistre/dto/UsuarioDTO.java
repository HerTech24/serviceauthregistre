package com.kairoscoffee.serviceauthregistre.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rol;
}