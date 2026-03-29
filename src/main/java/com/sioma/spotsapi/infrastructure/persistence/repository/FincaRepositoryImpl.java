package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.FincaEntity;
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
public class FincaRepositoryImpl implements FincaRepository {

    private final FincaJpaRepository jpaRepository;

    @Override
    @Transactional
    public Finca save(Finca finca) {
        log.debug("Guardando finca: {}", finca.getNombre());

        FincaEntity entity = new FincaEntity(finca.getNombre(), finca.getUsuarioId());
        entity = jpaRepository.save(entity);

        log.debug("Finca guardada con id: {}", entity.getId());
        return toDomain(entity);
    }

    @Override
    public Optional<Finca> findById(Long id) {
        log.debug("Buscando finca con id: {}", id);
        return jpaRepository.findById(id)
                .map(this::toDomain);
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
        log.debug("Buscando todas las fincas del usuario con id: {}", usuarioId);
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
