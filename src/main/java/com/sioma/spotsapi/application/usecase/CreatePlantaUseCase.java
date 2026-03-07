package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.PlantaAlreadyExistsException;
import org.springframework.stereotype.Service;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.domain.repository.PlantaRepository;

@Service
public class CreatePlantaUseCase {
    private final PlantaRepository repository;

    public CreatePlantaUseCase(PlantaRepository repository) {
        this.repository = repository;
    }

    public Planta execute(String nombre) {

        if(repository.existsByNombreIgnoreCase(nombre)) {
            throw new PlantaAlreadyExistsException();
        }

        Planta planta = new Planta(nombre);

        return repository.save(planta);
    }
}