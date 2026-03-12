package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.LoteEntity;
import org.springframework.stereotype.Repository;

@Repository
public class LoteRepositoryImpl implements LoteRepository {
    private final LoteJpaRepository jpaRepository;

    public LoteRepositoryImpl(LoteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Lote save(Lote lote) {
        LoteEntity entity = new LoteEntity(lote.getNombre(), lote.getFincaId(), lote.getTipoCultivoId());

        entity = jpaRepository.save(entity);

        return new Lote(entity.getId(), entity.getNombre(), entity.getFincaId(), entity.getTipoCultivoId());
    }

    @Override
    public boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId) {
        return jpaRepository.existsByNombreIgnoreCaseAndFincaId(nombre, fincaId);
    }
}
