package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.UpdateLoteUseCase;
import com.sioma.spotsapi.web.dto.UpdateLoteRequest;
import com.sioma.spotsapi.web.mapper.LoteResponseMapper;
import com.sioma.spotsapi.application.usecase.CreateLoteUseCase;
import com.sioma.spotsapi.application.usecase.DeleteLoteByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetLoteByIdUseCase;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.web.dto.CreateLoteRequest;
import com.sioma.spotsapi.web.dto.LoteResponse;
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
@RequestMapping("/lotes")
@Tag(name = "Lotes", description = "Gestión de lotes asociados a fincas")
public class LoteController {
    private final CreateLoteUseCase useCase;
    private final GetLoteByIdUseCase getLoteByIdUseCase;
    private final DeleteLoteByIdUseCase deleteLoteByIdUseCase;
    private final UpdateLoteUseCase updateLoteUseCase;
    private final LoteResponseMapper loteResponseMapper;


    @Operation(summary = "Crear un nuevo lote")
    @ApiResponse(responseCode = "201", description = "Lote creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Payload inválido o datos faltantes")
    @ApiResponse(responseCode = "409", description = "Nombre de lote duplicado para la finca")
    @PostMapping
    public ResponseEntity<LoteResponse> create(@Valid @RequestBody CreateLoteRequest request) {
        Lote lote = useCase.execute(
                request.nombre(),
                request.geocerca().coordinates().getFirst(), // List<List<Double>>
                request.fincaId(),
                request.tipoCultivoId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/lotes/" + lote.getId())
                .body(loteResponseMapper.toResponse(lote));
    }

    @Operation(summary = "Obtener lote por ID")
    @ApiResponse(responseCode = "200", description = "Lote encontrado")
    @ApiResponse(responseCode = "404", description = "Lote no existe")
    @GetMapping("/{id}")
    public ResponseEntity<LoteResponse> getById(
            @Parameter(description = "ID del lote", example = "5") @PathVariable @Min(1) Long id
    ) {
        return ResponseEntity.ok(
                loteResponseMapper.toResponse(
                        getLoteByIdUseCase.execute(id)
                )
        );
    }

    @Operation(summary = "Actualizar nombre de un lote existente")
    @ApiResponse(responseCode = "200", description = "Lote actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Parámetros de vacío o mal formado")
    @ApiResponse(responseCode = "409", description = "Nombre de lote duplicado para la finca")
    @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    @PatchMapping("/{id}/nombre")
    public ResponseEntity<LoteResponse> updateNombre(
            @Parameter(description = "ID del lote", example = "5") @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdateLoteRequest request
    ) {
        Lote lote = updateLoteUseCase.execute(id, request.nombre());

        return ResponseEntity.ok(loteResponseMapper.toResponse(lote));
    }

    @Operation(summary = "Eliminar un lote")
    @ApiResponse(responseCode = "204", description = "Lote eliminado")
    @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del lote", example = "5") @PathVariable @Min(1) Long id
    ) {
        deleteLoteByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
