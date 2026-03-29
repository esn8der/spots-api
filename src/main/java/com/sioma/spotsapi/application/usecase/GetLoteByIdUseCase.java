package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetLoteByIdUseCase {
    private final LoteRepository repository;

    public Lote execute(Long id) {
        log.debug("Buscando lote con id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new LoteNotFoundException(id));
    }
}
