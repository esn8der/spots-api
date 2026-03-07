package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.CreatePlantaUseCase;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.web.dto.CreatePlantaRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plantas")
public class PlantaController {
    private final CreatePlantaUseCase useCase;

    public PlantaController(CreatePlantaUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public Planta create(@RequestBody CreatePlantaRequest request) {
        return useCase.execute(request.nombre());
    }
}