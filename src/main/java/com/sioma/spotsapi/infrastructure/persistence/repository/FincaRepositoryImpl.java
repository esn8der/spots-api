package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import com.sioma.spotsapi.infrastructure.persistence.mapper.FincaEntityMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FincaRepositoryImpl implements FincaRepository {
    private final FincaJpaRepository jpaRepository;
    private final FincaEntityMapper mapper;

    @Override
    public Finca save(@NonNull Finca finca) {
        FincaEntity entity = mapper.toEntity(finca);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Finca> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
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
                .map(mapper::toDomain)
                .toList();
    }
}
