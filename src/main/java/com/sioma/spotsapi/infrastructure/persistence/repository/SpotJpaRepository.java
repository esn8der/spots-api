package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entity.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotJpaRepository extends JpaRepository<SpotEntity, Long> {
    boolean existsByLoteIdAndLineaAndPosicion(Long loteId, int linea, int posicion);
}
