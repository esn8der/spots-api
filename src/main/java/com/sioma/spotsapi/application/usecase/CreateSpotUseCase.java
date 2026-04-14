package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.exception.PointOutsideLoteException;
import com.sioma.spotsapi.domain.exception.SpotAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.SpotAlreadyExistsNearbyException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.model.SpotPosition;
import com.sioma.spotsapi.domain.ports.out.GeospatialConverter;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateSpotUseCase {
    private final SpotRepository spotRepository;
    private final LoteRepository loteRepository;
    private final GeospatialConverter geospatialConverter;

    @Transactional
    public Spot execute(List<Double> coordinates, Long loteId, int linea, int posicion) {
        log.debug("Creando spot con coordenada: {}, loteId: {}, linea: {}, posición: {}", coordinates, loteId, linea, posicion);

        Point point = geospatialConverter.toPoint(coordinates);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() ->
                        new LoteNotFoundException(loteId));

        if (spotRepository.existsByLoteIdAndLineaAndPosicion(loteId, linea, posicion)) {
            throw new SpotAlreadyExistsException(loteId, linea, posicion);
        }

        if (spotRepository.existsByLoteIdAndApproximateCoordinates(loteId, point.getX(), point.getY())) {
            throw new SpotAlreadyExistsNearbyException();
        }

        if (!lote.getGeocerca().contains(point)) {
            throw new PointOutsideLoteException();
        }

        Spot spot = lote.crearSpot(point, new SpotPosition(linea, posicion));

        log.info("Spot creado exitosamente con coordenada: {}, loteId: {}, linea: {}, posición: {}", coordinates, loteId, linea, posicion);
        return spotRepository.save(spot);
    }
}
