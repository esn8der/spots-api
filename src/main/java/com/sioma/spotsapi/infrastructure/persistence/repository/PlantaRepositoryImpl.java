package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.PlantaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlantaRepositoryImpl implements PlantaRepository {

    private final PlantaJpaRepository jpaRepository;

    @Override
    @Transactional
    public Planta save(Planta planta) {
        log.debug("Guardando planta: {}", planta.getNombre());

        PlantaEntity entity = new PlantaEntity(planta.getNombre());
        entity = jpaRepository.save(entity);

        log.debug("Planta guardada con id: {}", entity.getId());
        return toDomain(entity);
    }

    @Override
    public Optional<Planta> findById(Long id) {
        log.debug("Buscando planta con id: {}", id);
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByNombreIgnoreCase(String nombre) {
        return jpaRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Planta> findAll() {
        log.debug("Buscando todas las plantas");
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Planta toDomain(PlantaEntity entity) {
        return new Planta(entity.getId(), entity.getNombre());
    }
}