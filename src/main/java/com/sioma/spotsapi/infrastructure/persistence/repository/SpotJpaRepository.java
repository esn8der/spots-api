package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.infrastructure.persistence.entity.SpotEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpotJpaRepository extends JpaRepository<SpotEntity, Long> {
    boolean existsByLoteIdAndLineaAndPosicion(Long loteId, int linea, int posicion);

    Page<SpotEntity> findByLoteId(Long loteId, Pageable pageable);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM spot
                WHERE lote_id = :loteId
                  AND ROUND(ST_X(geo::geometry)::numeric, 6) = ROUND(CAST(:lon AS numeric), 6)
                  AND ROUND(ST_Y(geo::geometry)::numeric, 6) = ROUND(CAST(:lat AS numeric), 6)
            )
            """, nativeQuery = true)
    boolean existsByLoteIdAndApproximateCoordinates(
            @Param("loteId") Long loteId,
            @Param("lon") double lon,
            @Param("lat") double lat
    );
}
