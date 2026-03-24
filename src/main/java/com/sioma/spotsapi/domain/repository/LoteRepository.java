package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Lote;

import java.util.List;
import java.util.Optional;

public interface LoteRepository {
    Lote save(Lote lote);
    Optional<Lote> findById(Long id);
    boolean existsById(Long id);
    List<Lote> findAllByFincaId(Long fincaId);
    boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId);
}
