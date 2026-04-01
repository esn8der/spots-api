package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.exception.PointOutsideLoteException;
import com.sioma.spotsapi.domain.exception.SpotAlreadyExistsException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CreateSpotUseCase {
    private final SpotRepository spotRepository;
    private final LoteRepository loteRepository;

    public CreateSpotUseCase(SpotRepository spotRepository, LoteRepository loteRepository) {
        this.spotRepository = spotRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional
    public Spot execute(Point coordenada, Long loteId, int linea, int posicion) {

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new LoteNotFoundException(loteId));

        if (spotRepository.existsByLoteIdAndLineaAndPosicion(loteId, linea, posicion)) {
            throw new SpotAlreadyExistsException();
        }
        if (!lote.getGeocerca().contains(coordenada)) {
            throw new PointOutsideLoteException();
        }

        return spotRepository.save(new Spot(coordenada, loteId, linea, posicion));
    }
}
