package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.PlantaAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePlantaUseCase {
    private final PlantaRepository repository;

    @Transactional
    public Planta execute(String nombre) {
        log.debug("Creando planta con nombre: {}", nombre);

        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new PlantaAlreadyExistsException(nombre);
        }

        Planta planta = new Planta(nombre);

        log.info("Planta creada exitosamente con nombre: {}", nombre);
        return repository.save(planta);
    }
}