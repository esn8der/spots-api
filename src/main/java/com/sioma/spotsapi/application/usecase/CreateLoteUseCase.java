package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotExistsException;
import com.sioma.spotsapi.domain.exception.LoteAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.PlantaNotExistsException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

@Service
public class CreateLoteUseCase {
    private final LoteRepository repository;
    private final FincaRepository fincaRepository;
    private final PlantaRepository plantaRepository;

    public CreateLoteUseCase(LoteRepository repository, FincaRepository fincaRepository, PlantaRepository plantaRepository) {
        this.repository = repository;
        this.fincaRepository = fincaRepository;
        this.plantaRepository = plantaRepository;
    }

    public Lote execute(String nombre, Polygon geocerca, Long fincaId, Long tipoCultivoId) {

        if(!fincaRepository.existsById(fincaId)) {
            throw new FincaNotExistsException(fincaId);
        }

        if(!plantaRepository.existsById(tipoCultivoId)) {
            throw new PlantaNotExistsException(tipoCultivoId);
        }

        if(repository.existsByNombreIgnoreCaseAndFincaId(nombre, fincaId)) {
            throw new LoteAlreadyExistsException();
        }

        Lote lote = new Lote(nombre, geocerca, fincaId, tipoCultivoId);

        return repository.save(lote);
    }
}
