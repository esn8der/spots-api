package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.SpotNotFoundException;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetSpotByIdUseCase {
    private final SpotRepository repository;

    public Spot execute(Long id) {
        log.debug("Buscando spot con id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));
    }
}
