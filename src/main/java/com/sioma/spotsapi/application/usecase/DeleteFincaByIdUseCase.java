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
        log.debug("Intentando eliminar finca con id: {}", id);

        repository.findById(id)
                .orElseThrow(() -> new FincaNotFoundException(id));

        repository.deleteById(id);
        log.info("Finca con id: {} eliminada exitosamente", id);
    }
}
