package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FincaJpaRepository extends JpaRepository<FincaEntity, Long> {
    boolean existsByNombreIgnoreCaseAndIdUsuario(String nombre, Long idUsuario);
}
