package com.kairoscoffee.serviceauthregistre.controller;

import com.kairoscoffee.serviceauthregistre.dto.UsuarioDTO;
import com.kairoscoffee.serviceauthregistre.entity.Usuario;
import com.kairoscoffee.serviceauthregistre.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserAdminService adminService;

    @GetMapping
    public List<Usuario> getAllUsers() {
        return adminService.getAll();
    }

    @GetMapping("/{id}")
    public Usuario getUser(@PathVariable Long id) {
        return adminService.getById(id);
    }

    @PutMapping("/{id}")
    public Usuario updateUser(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return adminService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminService.delete(id);
        return "Usuario eliminado";
    }
}
