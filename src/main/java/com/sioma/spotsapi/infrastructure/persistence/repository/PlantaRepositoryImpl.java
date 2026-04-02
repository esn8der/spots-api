package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.PlantaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlantaRepositoryImpl implements PlantaRepository {

    private final PlantaJpaRepository jpaRepository;

    @Override
    public Planta save(Planta planta) {
        PlantaEntity entity = new PlantaEntity(planta.getNombre());
        entity = jpaRepository.save(entity);

        return toDomain(entity);
    }

    @Override
    public Optional<Planta> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByNombreIgnoreCase(String nombre) {
        return jpaRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public List<Planta> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Planta toDomain(PlantaEntity entity) {
        return new Planta(entity.getId(), entity.getNombre());
    }
}