package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.SpotEntity;
import org.springframework.stereotype.Repository;

@Repository
public class SpotRepositoryImpl implements SpotRepository {
    private final SpotJpaRepository jpaRepository;

    public SpotRepositoryImpl(SpotJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    @Override
    public Spot save(Spot spot) {
        SpotEntity entity = new SpotEntity(
                spot.getCoordenada(),
                spot.getLoteId(),
                spot.getLinea(),
                spot.getPosicion()
        );
        entity = jpaRepository.save(entity);

        return toDomain(entity);
    }

    @Override
    public boolean existsByLoteIdAndLineaAndPosicion(Long loteId, int linea, int posicion) {
        return jpaRepository.existsByLoteIdAndLineaAndPosicion(loteId, linea, posicion);
    }

    private Spot toDomain(SpotEntity entity) {
        return new Spot(
                entity.getId(),
                entity.getCoordenada(),
                entity.getLoteId(),
                entity.getLinea(),
                entity.getPosicion()
        );
    }
}
