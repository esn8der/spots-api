package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.mapper.PlantaMapper;
import com.sioma.spotsapi.application.usecase.CreatePlantaUseCase;
import com.sioma.spotsapi.application.usecase.GetPlantaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetPlantasUseCase;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.web.dto.CreatePlantaRequest;
import com.sioma.spotsapi.web.dto.PlantaResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/plantas")
public class PlantaController {
    private final CreatePlantaUseCase createPlantaUseCase;
    private final GetPlantasUseCase getPlantasUseCase;
    private final GetPlantaByIdUseCase getPlantaByIdUseCase;
    private final PlantaMapper plantaMapper;

    @PostMapping
    public ResponseEntity<PlantaResponse> create(@Valid @RequestBody CreatePlantaRequest request) {
        Planta planta = createPlantaUseCase.execute(request.nombre());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/plantas/" + planta.getNombre())
                .body(
                        plantaMapper.toResponse(planta)
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantaResponse> getById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(
                plantaMapper.toResponse(
                        getPlantaByIdUseCase.execute(id)
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<PlantaResponse>> getAll() {
        return ResponseEntity.ok(
                plantaMapper.toResponseList(
                        getPlantasUseCase.execute()
                )
        );
    }
}