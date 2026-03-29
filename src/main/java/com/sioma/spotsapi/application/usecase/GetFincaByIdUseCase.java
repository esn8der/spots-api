package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetFincaByIdUseCase {
    private final FincaRepository repository;

    public Finca execute(Long id) {
        log.debug("Buscando finca con id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new FincaNotFoundException(id));
    }
}
