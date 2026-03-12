package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Lote;

public interface LoteRepository {
    Lote save(Lote lote);
    boolean existsByNombreIgnoreCaseAndFincaId(String nombre, Long fincaId);
}
