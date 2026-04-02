package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPlantasUseCase {
    private final PlantaRepository repository;

    public List<Planta> execute() {
        log.debug("Buscando todas las plantas");
        return repository.findAll();
    }
}
