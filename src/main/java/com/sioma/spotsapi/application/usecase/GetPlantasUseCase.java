package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetPlantasUseCase {
    private final PlantaRepository repository;

    public GetPlantasUseCase(PlantaRepository repository) {
        this.repository = repository;
    }

    public List<Planta> execute() {
        return repository.findAll();
    }
}
