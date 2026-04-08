package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.PlantaNotFoundException;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletePlantaByIdUseCase {
    private final PlantaRepository repository;

    @Transactional
    public void execute(Long id) {
        log.debug("Intentando eliminar planta con id: {}", id);

        repository.findById(id)
                .orElseThrow(() -> new PlantaNotFoundException(id));

        repository.deleteById(id);
        log.info("Planta con id: {} eliminada exitosamente", id);
    }
}
