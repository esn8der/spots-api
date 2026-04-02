package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.FincaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FincaRepositoryImpl implements FincaRepository {

    private final FincaJpaRepository jpaRepository;

    @Override
    public Finca save(Finca finca) {
        FincaEntity entity = new FincaEntity(finca.getNombre(), finca.getUsuarioId());
        entity = jpaRepository.save(entity);

        return toDomain(entity);
    }

    @Override
    public Optional<Finca> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByNombreIgnoreCaseAndUsuarioId(String nombre, Long usuarioId) {
        return jpaRepository.existsByNombreIgnoreCaseAndUsuarioId(nombre, usuarioId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Finca> findAllByUsuarioId(Long usuarioId) {
        return jpaRepository.findAllByUsuarioId(usuarioId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Finca toDomain(FincaEntity entity) {
        return new Finca(
                entity.getId(),
                entity.getNombre(),
                entity.getUsuarioId()
        );
    }
}
