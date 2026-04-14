package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.PaginationParams;
import com.sioma.spotsapi.domain.model.Spot;

import java.util.Optional;

public interface SpotRepository {
    Spot save(Spot spot);
    void deleteById(Long id);
    Optional<Spot> findById(Long id);
    PageResult<Spot> findByLoteId(Long loteId, PaginationParams params);
    boolean existsByLoteIdAndLineaAndPosicion(Long loteId, int linea, int posicion);
    boolean existsByLoteIdAndApproximateCoordinates(Long loteId, double longitude, double latitude);
}
