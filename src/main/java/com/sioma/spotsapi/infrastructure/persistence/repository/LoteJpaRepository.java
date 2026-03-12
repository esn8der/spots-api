package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entity.LoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteJpaRepository extends JpaRepository<LoteEntity, Long> {
    boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId);
}
