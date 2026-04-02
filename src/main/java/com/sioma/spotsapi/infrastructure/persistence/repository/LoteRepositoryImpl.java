package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.LoteEntity;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LoteRepositoryImpl implements LoteRepository {
    private final LoteJpaRepository jpaRepository;

    @Override
    public Lote save(Lote lote) {
        LoteEntity entity = new LoteEntity(
                lote.getNombre(),
                (Polygon) lote.getGeocerca(),
                lote.getFincaId(),
                lote.getTipoCultivoId()
        );
        entity = jpaRepository.save(entity);

        return toDomain(entity);
    }

    @Override
    public Optional<Lote> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Lote> findAllByFincaId(Long fincaId) {
        return jpaRepository.findAllByFincaId(fincaId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId) {
        return jpaRepository.existsByNombreIgnoreCaseAndFincaId(nombre, fincaId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Lote toDomain(LoteEntity entity) {
        return new Lote(
                entity.getId(),
                entity.getNombre(),
                entity.getGeocerca(),
                entity.getFincaId(),
                entity.getTipoCultivoId()
        );
    }
}
