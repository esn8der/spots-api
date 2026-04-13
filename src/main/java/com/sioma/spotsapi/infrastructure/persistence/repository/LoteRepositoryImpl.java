package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.PaginationParams;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.LoteEntity;
import com.sioma.spotsapi.infrastructure.persistence.mapper.LoteEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LoteRepositoryImpl implements LoteRepository {
    private final LoteJpaRepository jpaRepository;
    private final LoteEntityMapper mapper;

    @Override
    public Lote save(Lote lote) {
        LoteEntity entity = mapper.toEntity(lote);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Lote> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public PageResult<Lote> findAllByFincaId(Long fincaId, PaginationParams params) {
        Sort sort = Sort.by(Sort.Direction.fromString(params.sortDir()), params.sortBy());
        Pageable pageable = PageRequest.of(params.page(), params.size(), sort);

        Page<LoteEntity> pageResult = jpaRepository.findAllByFincaId(fincaId, pageable);
        List<Lote> lotes = pageResult.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(
                lotes,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    public boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId) {
        return jpaRepository.existsByNombreIgnoreCaseAndFincaId(nombre, fincaId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
