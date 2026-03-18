package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Lote;

import java.util.List;

public interface LoteRepository {
    Lote save(Lote lote);
    List<Lote> findAllByFincaId(Long fincaId);
    boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId);
}
