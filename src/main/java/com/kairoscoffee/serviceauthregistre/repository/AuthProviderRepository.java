package com.kairoscoffee.serviceauthregistre.repository;

import com.kairoscoffee.serviceauthregistre.entity.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {

    Optional<AuthProvider> findByProviderAndProviderUserId(String provider, String providerUserId);

    Optional<AuthProvider> findByUsuario_Id(Long usuarioId);
}
