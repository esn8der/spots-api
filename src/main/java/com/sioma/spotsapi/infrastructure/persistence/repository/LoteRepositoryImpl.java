package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.LoteEntity;
import com.sioma.spotsapi.infrastructure.persistence.mapper.LoteEntityMapper;
import lombok.RequiredArgsConstructor;
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
    public List<Lote> findAllByFincaId(Long fincaId) {
        return jpaRepository.findAllByFincaId(fincaId)
                .stream()
                .map(mapper::toDomain)
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
}
