package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FincaJpaRepository extends JpaRepository<FincaEntity, Long> {
    List<FincaEntity> findAllByUsuarioId(Long usuarioId);
    boolean existsByNombreIgnoreCaseAndUsuarioId(String nombre, Long usuarioId);
}
