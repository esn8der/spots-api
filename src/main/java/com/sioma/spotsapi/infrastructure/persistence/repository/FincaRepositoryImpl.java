package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.PaginationParams;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import com.sioma.spotsapi.infrastructure.persistence.mapper.FincaEntityMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResult<Finca> findAllByUsuarioId(Long usuarioId, PaginationParams params) {
        Sort sort = Sort.by(Sort.Direction.fromString(params.sortDir()), params.sortBy());
        Pageable pageable = PageRequest.of(params.page(), params.size(), sort);

        Page<FincaEntity> pageResult = jpaRepository.findAllByUsuarioId(usuarioId, pageable);
        List<Finca> fincas = pageResult.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(
                fincas,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }
}
