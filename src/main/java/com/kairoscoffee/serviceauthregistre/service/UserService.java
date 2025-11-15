package com.kairoscoffee.serviceauthregistre.service;

import com.kairoscoffee.serviceauthregistre.entity.Rol;
import com.kairoscoffee.serviceauthregistre.entity.Usuario;
import com.kairoscoffee.serviceauthregistre.repository.RolRepository;
import com.kairoscoffee.serviceauthregistre.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Usuario> findByEmail(String email){
        return usuarioRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario createUserLocal(String nombre, String apellido, String email, String telefono, String password){

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setTelefono(telefono);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setFechaRegistro(new Date());

        Rol rol = rolRepository.findByNombre("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Rol CUSTOMER no existe"));

        u.setRol(rol);

        return usuarioRepository.save(u);
    }
}
