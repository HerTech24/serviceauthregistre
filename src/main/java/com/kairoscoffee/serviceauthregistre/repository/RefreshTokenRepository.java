package com.kairoscoffee.serviceauthregistre.repository;

import com.kairoscoffee.serviceauthregistre.entity.RefreshToken;
import com.kairoscoffee.serviceauthregistre.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUsuario(Usuario usuario);

    boolean existsByUsuario(Usuario usuario);
}
