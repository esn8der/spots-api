package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.mapper.PlantaMapper;
import com.sioma.spotsapi.application.usecase.CreatePlantaUseCase;
import com.sioma.spotsapi.application.usecase.GetPlantasUseCase;
import com.sioma.spotsapi.web.dto.CreatePlantaRequest;
import com.sioma.spotsapi.web.dto.PlantaResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plantas")
public class PlantaController {
    private final CreatePlantaUseCase createPlantaUseCase;
    private final GetPlantasUseCase getPlantasUseCase;
    private final PlantaMapper plantaMapper;

    public PlantaController(
            CreatePlantaUseCase useCase,
            GetPlantasUseCase getPlantasUseCase,
            PlantaMapper plantaMapper) {
        this.createPlantaUseCase = useCase;
        this.getPlantasUseCase = getPlantasUseCase;
        this.plantaMapper = plantaMapper;
    }

    @PostMapping
    public PlantaResponse create(@RequestBody CreatePlantaRequest request) {
        return plantaMapper.toResponse(
                createPlantaUseCase.execute(request.nombre())
        );
    }

    @GetMapping
    public List<PlantaResponse> getAll() {
        return plantaMapper.toResponseList(
                getPlantasUseCase.execute()
        );
    }
}