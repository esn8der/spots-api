package com.sioma.spotsapi.web.controller;

import org.springframework.web.bind.annotation.*;
import com.sioma.spotsapi.web.dto.PlantaResponse;
import com.sioma.spotsapi.application.usecase.CreatePlantaUseCase;
import com.sioma.spotsapi.application.usecase.GetPlantasUseCase;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.web.dto.CreatePlantaRequest;
import java.util.List;

@RestController
@RequestMapping("/plantas")
public class PlantaController {
    private final CreatePlantaUseCase createPlantaUseCase;
    private final GetPlantasUseCase getPlantasUseCase;

    public PlantaController(CreatePlantaUseCase useCase, GetPlantasUseCase getPlantasUseCase) {
        this.createPlantaUseCase = useCase;
        this.getPlantasUseCase = getPlantasUseCase;
    }

    @PostMapping
    public PlantaResponse create(@RequestBody CreatePlantaRequest request) {
        Planta planta = createPlantaUseCase.execute(request.nombre());

        return new PlantaResponse(
                planta.getId(),
                planta.getNombre()
        );
    }

    @GetMapping
    public List<PlantaResponse> getAll() {
        return getPlantasUseCase.execute()
                .stream()
                .map(planta -> new PlantaResponse(
                        planta.getId(),
                        planta.getNombre()
                ))
                .toList();
    }
}