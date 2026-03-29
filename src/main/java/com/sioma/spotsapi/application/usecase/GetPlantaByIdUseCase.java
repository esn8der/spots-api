package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.PlantaNotFoundException;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPlantaByIdUseCase {
    private final PlantaRepository repository;

    public Planta execute(Long id) {
        log.debug("Buscando planta con id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new PlantaNotFoundException(id));
    }
}
