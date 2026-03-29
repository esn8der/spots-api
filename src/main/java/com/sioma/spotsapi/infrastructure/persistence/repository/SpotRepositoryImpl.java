package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.SpotEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotRepositoryImpl implements SpotRepository {
    private final SpotJpaRepository jpaRepository;

    @Override
    @Transactional
    public Spot save(Spot spot) {
        log.debug("Guardando spot: {}", spot.getCoordenada());
        SpotEntity entity = new SpotEntity(
                spot.getCoordenada(),
                spot.getLoteId(),
                spot.getLinea(),
                spot.getPosicion()
        );
        entity = jpaRepository.save(entity);

        log.debug("Spot guardado con id: {}", entity.getId());
        return toDomain(entity);
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
