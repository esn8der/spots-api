package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.PaginationParams;

import java.util.Optional;

public interface LoteRepository {
    Lote save(Lote lote);
    void deleteById(Long id);
    Optional<Lote> findById(Long id);
    PageResult<Lote> findAllByFincaId(Long fincaId, PaginationParams params);
    boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId);
}
