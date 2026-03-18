package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.PlantaEntity;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PlantaRepositoryImpl implements PlantaRepository {

    private final PlantaJpaRepository jpaRepository;

    public PlantaRepositoryImpl(PlantaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Planta save(Planta planta) {

        PlantaEntity entity = new PlantaEntity(planta.getNombre());

        entity = jpaRepository.save(entity);

        return new Planta(entity.getId(), entity.getNombre());
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
        return jpaRepository.findAll()
                .stream()
                .map(entity -> new Planta(
                        entity.getId(),
                        entity.getNombre()
                ))
                .toList();
    }
}