package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.PageResult;

import java.util.Optional;

public interface LoteRepository {
    Lote save(Lote lote);
    void deleteById(Long id);
    Optional<Lote> findById(Long id);
    PageResult<Lote> findAllByFincaId(Long fincaId, int page, int size);
    boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId);
}
