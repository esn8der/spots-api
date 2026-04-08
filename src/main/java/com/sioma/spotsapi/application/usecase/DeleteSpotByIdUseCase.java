package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.SpotNotFoundException;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteSpotByIdUseCase {
    private final SpotRepository repository;

    @Transactional
    public void execute(Long id) {
        log.debug("Intentando eliminar spot con id: {}", id);

        repository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        repository.deleteById(id);
        log.info("Spot con id: {} eliminada exitosamente", id);
    }
}
