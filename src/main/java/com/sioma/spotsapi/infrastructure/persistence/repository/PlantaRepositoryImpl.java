package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.PlantaEntity;
import com.sioma.spotsapi.infrastructure.persistence.mapper.PlantaEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlantaRepositoryImpl implements PlantaRepository {
    private final PlantaJpaRepository jpaRepository;
    private final PlantaEntityMapper mapper;

    @Override
    public Planta save(Planta planta) {
        PlantaEntity entity = mapper.toEntity(planta);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Planta> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByNombreIgnoreCase(String nombre) {
        return jpaRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public List<Planta> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}