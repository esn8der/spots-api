package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entity.SpotEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotJpaRepository extends JpaRepository<SpotEntity, Long> {
    boolean existsByLoteIdAndLineaAndPosicion(Long loteId, int linea, int posicion);
    Page<SpotEntity> findByLoteId(Long loteId, Pageable pageable);
}
