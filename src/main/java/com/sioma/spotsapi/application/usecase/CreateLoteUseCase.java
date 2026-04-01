package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.exception.LoteAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.PlantaNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Lote execute(String nombre, Polygon geocerca, Long fincaId, Long tipoCultivoId) {

        if (fincaRepository.findById(fincaId).isEmpty()) {
            throw new FincaNotFoundException(fincaId);
        }
        if (plantaRepository.findById(tipoCultivoId).isEmpty()) {
            throw new PlantaNotFoundException(tipoCultivoId);
        }
        if (repository.existsByNombreIgnoreCaseAndFincaId(nombre, fincaId)) {
            throw new LoteAlreadyExistsException();
        }

        Lote lote = new Lote(nombre, geocerca, fincaId, tipoCultivoId);

        return repository.save(lote);
    }
}
