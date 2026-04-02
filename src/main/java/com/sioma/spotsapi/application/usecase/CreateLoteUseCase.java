package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.exception.LoteAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.PlantaNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateLoteUseCase {
    private final LoteRepository repository;
    private final FincaRepository fincaRepository;
    private final PlantaRepository plantaRepository;

    @Transactional
    public Lote execute(String nombre, Polygon geocerca, Long fincaId, Long tipoCultivoId) {
        log.debug("Creando lote con nombre: {}, fincaId: {}, tipoCultivoId: {}", nombre, fincaId, tipoCultivoId);

        if (fincaRepository.findById(fincaId).isEmpty()) {
            throw new FincaNotFoundException(fincaId);
        }
        if (plantaRepository.findById(tipoCultivoId).isEmpty()) {
            throw new PlantaNotFoundException(tipoCultivoId);
        }
        if (repository.existsByNombreIgnoreCaseAndFincaId(nombre, fincaId)) {
            throw new LoteAlreadyExistsException(nombre, fincaId);
        }

        Lote lote = new Lote(nombre, geocerca, fincaId, tipoCultivoId);

        log.info("Lote creado exitosamente con nombre: {}", nombre);
        return repository.save(lote);
    }
}
