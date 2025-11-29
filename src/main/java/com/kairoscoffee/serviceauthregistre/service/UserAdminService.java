package com.kairoscoffee.serviceauthregistre.service;

import com.kairoscoffee.serviceauthregistre.dto.UsuarioDTO;
import com.kairoscoffee.serviceauthregistre.entity.Rol;
import com.kairoscoffee.serviceauthregistre.entity.Usuario;
import com.kairoscoffee.serviceauthregistre.repository.RolRepository;
import com.kairoscoffee.serviceauthregistre.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    public Usuario getById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario update(Long id, UsuarioDTO dto) {

        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setTelefono(dto.getTelefono());

        if (dto.getRol() != null) {
            Rol rol = rolRepository.findByNombre(dto.getRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            u.setRol(rol);
        }

        return usuarioRepository.save(u);
    }

    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no existe");
        }
        usuarioRepository.deleteById(id);
    }
}
