package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Spot;

public interface SpotRepository {
    Spot save(Spot spot);
    boolean existsByLoteIdAndLineaAndPosicion(Long loteId, int linea, int posicion);
}
