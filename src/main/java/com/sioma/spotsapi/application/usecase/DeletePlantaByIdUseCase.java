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
        if (id == null || id <= 0) {
            log.error("ID de planta inválido para eliminación: {}", id);
            throw new IllegalArgumentException("ID de planta inválido: " + id);
        }

        log.debug("Intentando eliminar planta con id: {}", id);

        if (repository.findById(id).isEmpty()) {
            log.warn("Planta con id: {} no encontrada para eliminar", id);
            throw new PlantaNotFoundException(id);
        }

        repository.deleteById(id);
        log.info("Planta con id: {} eliminada exitosamente", id);
    }
}
