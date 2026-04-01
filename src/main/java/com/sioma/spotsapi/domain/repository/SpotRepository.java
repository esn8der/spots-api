package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Spot;

import java.util.Optional;

public interface SpotRepository {
    Spot save(Spot spot);
    void deleteById(Long id);
    Optional<Spot> findById(Long id);
    boolean existsByLoteIdAndLineaAndPosicion(Long loteId, int linea, int posicion);
}
