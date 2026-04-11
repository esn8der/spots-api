package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entity.LoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteJpaRepository extends JpaRepository<LoteEntity, Long> {
    boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId);
    Page<LoteEntity> findAllByFincaId(Long fincaId, Pageable pageable);
}
