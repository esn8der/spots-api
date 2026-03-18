package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FincaRepositoryImpl implements FincaRepository {

    private final FincaJpaRepository jpaRepository;

    public FincaRepositoryImpl(FincaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Finca save (Finca finca){

        FincaEntity entity = new FincaEntity(finca.getNombre(), finca.getUsuarioId());

        entity = jpaRepository.save(entity);

        return new Finca(entity.getId(), entity.getNombre(), entity.getUsuarioId());
    }

    @Override
    public boolean existsByNombreIgnoreCaseAndUsuarioId(String nombre, Long usuarioId) {
        return jpaRepository.existsByNombreIgnoreCaseAndUsuarioId(nombre, usuarioId);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Finca> findAllByUsuarioId(Long usuarioId) {
        return jpaRepository.findAllByUsuarioId(usuarioId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    // mapper simple (luego usar MapStruct)
    private Finca toDomain(FincaEntity entity) {
        return new Finca(
                entity.getId(),
                entity.getNombre(),
                entity.getUsuarioId()
        );
    }
}
