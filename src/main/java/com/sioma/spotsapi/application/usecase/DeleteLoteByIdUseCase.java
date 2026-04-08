package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteLoteByIdUseCase {
    private final LoteRepository repository;

    @Transactional
    public void execute(Long id) {
        log.debug("Intentando eliminar Lote con id: {}", id);

        repository.findById(id)
                .orElseThrow(() -> new LoteNotFoundException(id));

        repository.deleteById(id);
        log.info("Lote con id: {} eliminado exitosamente", id);
    }
}
