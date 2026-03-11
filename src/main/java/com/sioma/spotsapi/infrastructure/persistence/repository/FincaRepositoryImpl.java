package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.exception.FincaAlreadyExistsException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import org.springframework.stereotype.Repository;

@Repository
public class FincaRepositoryImpl implements FincaRepository {

    private final FincaJpaRepository jpaRepository;

    public FincaRepositoryImpl(FincaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Finca save (Finca finca){

        FincaEntity entity = new FincaEntity(finca.getNombre(), finca.getIdUsuario());

        entity = jpaRepository.save(entity);

        return new Finca(entity.getId(), entity.getNombre(), entity.getIdUsuario());
    }

    @Override
    public boolean existsByNombreIgnoreCaseAndIdUsuario(String nombre, Long idUsuario) {
        return jpaRepository.existsByNombreIgnoreCaseAndIdUsuario(nombre, idUsuario);
    }
}
