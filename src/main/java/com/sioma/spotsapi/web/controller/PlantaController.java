package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.web.mapper.PlantaResponseMapper;
import com.sioma.spotsapi.application.usecase.CreatePlantaUseCase;
import com.sioma.spotsapi.application.usecase.DeletePlantaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetPlantaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetPlantasUseCase;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.web.dto.CreatePlantaRequest;
import com.sioma.spotsapi.web.dto.PlantaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Plantas", description = "Gestión de plantas")
public class PlantaController {
    private final CreatePlantaUseCase createPlantaUseCase;
    private final GetPlantasUseCase getPlantasUseCase;
    private final GetPlantaByIdUseCase getPlantaByIdUseCase;
    private final DeletePlantaByIdUseCase deletePlantaByIdUseCase;
    private final PlantaResponseMapper plantaResponseMapper;

    @Operation(summary = "Crear una nueva planta")
    @ApiResponse(responseCode = "201", description = "Planta creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Payload inválido o datos faltantes")
    @ApiResponse(responseCode = "409", description = "Nombre de Planta duplicado")
    @PostMapping
    public ResponseEntity<PlantaResponse> create(@Valid @RequestBody CreatePlantaRequest request) {
        Planta planta = createPlantaUseCase.execute(request.nombre());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/plantas/" + planta.getNombre())
                .body(
                        plantaResponseMapper.toResponse(planta)
                );
    }

    @Operation(summary = "Obtener planta por ID")
    @ApiResponse(responseCode = "200", description = "Planta encontrada")
    @ApiResponse(responseCode = "404", description = "Planta no existe")
    @GetMapping("/{id}")
    public ResponseEntity<PlantaResponse> getById(
            @Parameter(description = "ID de la planta", example = "5") @PathVariable @Min(1) Long id
    ) {
        return ResponseEntity.ok(
                plantaResponseMapper.toResponse(
                        getPlantaByIdUseCase.execute(id)
                )
        );
    }

    @Operation(summary = "Obtener planta por ID")
    @ApiResponse(responseCode = "200", description = "Planta encontrada")
    @GetMapping
    public ResponseEntity<List<PlantaResponse>> getAll() {

        return ResponseEntity.ok(
                plantaResponseMapper.toResponseList(
                        getPlantasUseCase.execute()
                )
        );
    }

    @Operation(summary = "Eliminar una planta")
    @ApiResponse(responseCode = "204", description = "Planta eliminada")
    @ApiResponse(responseCode = "404", description = "Planta no encontrada")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la planta", example = "5") @PathVariable @Min(1) Long id
    ) {
        deletePlantaByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}