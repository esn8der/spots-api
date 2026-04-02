package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.SpotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SpotRepositoryImpl implements SpotRepository {
    private final SpotJpaRepository jpaRepository;

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
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Spot> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
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
