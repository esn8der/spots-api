package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteFincaByIdUseCase {
    private final FincaRepository repository;

    @Transactional
    public void execute(Long id) {
        if (id == null || id <= 0) {
            log.warn("ID de finca inválido para eliminación: {}", id);
            throw new IllegalArgumentException("ID de finca inválido: " + id);
        }

        log.debug("Intentando eliminar finca con id: {}", id);

        if (repository.findById(id).isEmpty()) {
            throw new FincaNotFoundException(id);
        }

        repository.deleteById(id);
        log.info("Finca con id: {} eliminada exitosamente", id);
    }
}
