package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.SpotEntity;
import com.sioma.spotsapi.infrastructure.persistence.mapper.SpotEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SpotRepositoryImpl implements SpotRepository {
    private final SpotJpaRepository jpaRepository;
    private final SpotEntityMapper mapper;

    @Override
    public Spot save(Spot spot) {
        SpotEntity entity = mapper.toEntity(spot);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Spot> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByLoteIdAndLineaAndPosicion(Long loteId, int linea, int posicion) {
        return jpaRepository.existsByLoteIdAndLineaAndPosicion(loteId, linea, posicion);
    }
}
