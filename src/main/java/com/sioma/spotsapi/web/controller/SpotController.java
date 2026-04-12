package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.web.mapper.SpotResponseMapper;
import com.sioma.spotsapi.application.usecase.CreateSpotUseCase;
import com.sioma.spotsapi.application.usecase.DeleteSpotByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetSpotByIdUseCase;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.web.dto.CreateSpotRequest;
import com.sioma.spotsapi.web.dto.SpotResponse;
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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/spots")
@Tag(name = "Spots", description = "Gestión de Spots asociados a lotes")
public class SpotController {
    private final CreateSpotUseCase useCase;
    private final GetSpotByIdUseCase getSpotByIdUseCase;
    private final DeleteSpotByIdUseCase deleteSpotByIdUseCase;
    private final SpotResponseMapper spotResponseMapper;

    @Operation(summary = "Crear un nuevo spot")
    @ApiResponse(responseCode = "201", description = "Spot creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Payload inválido o datos faltantes")
    @ApiResponse(responseCode = "409", description = "Spot duplicado por lote, línea y posición")
    @PostMapping
    public ResponseEntity<SpotResponse> create(@Valid @RequestBody CreateSpotRequest request)
    {
        Spot spot = useCase.execute(
                request.coordenada().coordinates(), // List<Double>
                request.loteId(),
                request.linea(),
                request.posicion()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/spots/" + spot.getId())
                .body(spotResponseMapper.toResponse(spot));
    }

    @Operation(summary = "Obtener spot por ID")
    @ApiResponse(responseCode = "200", description = "Spot encontrado")
    @ApiResponse(responseCode = "404", description = "Spot no existe")
    @GetMapping("/{id}")
    public ResponseEntity<SpotResponse> getById(
            @Parameter(description = "ID del spot", example = "5") @PathVariable Long id)
    {
        return ResponseEntity.ok(
                spotResponseMapper.toResponse(
                        getSpotByIdUseCase.execute(id)
                )
        );
    }

    @Operation(summary = "Eliminar un spot")
    @ApiResponse(responseCode = "204", description = "Spot eliminado")
    @ApiResponse(responseCode = "404", description = "Spot no encontrado")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del spot", example = "5") @PathVariable @Min(1) Long id
    ) {
        deleteSpotByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
