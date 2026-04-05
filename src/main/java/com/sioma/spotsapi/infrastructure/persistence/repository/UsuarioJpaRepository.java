package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
