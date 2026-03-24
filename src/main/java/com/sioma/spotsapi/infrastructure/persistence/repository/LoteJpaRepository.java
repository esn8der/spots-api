package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entity.LoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoteJpaRepository extends JpaRepository<LoteEntity, Long> {
    boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId);
    List<LoteEntity> findAllByFincaId(Long fincaId);
    boolean existsById(Long id);
    Optional<LoteEntity> findById(Long id);
}
