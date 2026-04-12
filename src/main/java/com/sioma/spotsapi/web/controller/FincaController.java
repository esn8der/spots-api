package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.web.dto.PageResponse;
import com.sioma.spotsapi.web.mapper.FincaResponseMapper;
import com.sioma.spotsapi.web.mapper.LoteResponseMapper;
import com.sioma.spotsapi.application.usecase.CreateFincaUseCase;
import com.sioma.spotsapi.application.usecase.DeleteFincaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetFincaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetLotesByFincaIdUseCase;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.web.dto.CreateFincaRequest;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.web.dto.LoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
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
@RequestMapping("/fincas")
@Tag(name = "Fincas", description = "Gestión de fincas agrícolas vinculadas a usuarios")
public class FincaController {
    private final CreateFincaUseCase useCase;
    private final GetLotesByFincaIdUseCase getLotesByFincaIdUseCase;
    private final GetFincaByIdUseCase getFincaByIdUseCase;
    private final DeleteFincaByIdUseCase deleteFincaByIdUseCase;
    private final FincaResponseMapper fincaResponseMapper;
    private final LoteResponseMapper loteResponseMapper;

    @Operation(summary = "Crear una nueva finca")
    @ApiResponse(responseCode = "201", description = "Finca creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Payload inválido o datos faltantes")
    @ApiResponse(responseCode = "409", description = "Nombre de finca duplicado para el usuario")
    @PostMapping
    public ResponseEntity<FincaResponse> create(@Valid @RequestBody CreateFincaRequest request) {
        Finca finca = useCase.execute(request.nombre(), request.usuarioId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/fincas/" + finca.getId())
                .body(fincaResponseMapper.toResponse(finca));
    }

    @Operation(summary = "Obtener finca por ID")
    @ApiResponse(responseCode = "200", description = "Finca encontrada")
    @ApiResponse(responseCode = "404", description = "Finca no existe")
    @GetMapping("/{id}")
    public ResponseEntity<FincaResponse> getById(
            @Parameter(description = "ID de la finca", example = "5") @PathVariable @Min(1) Long id
    ) {
        return ResponseEntity.ok(
                fincaResponseMapper.toResponse(
                        getFincaByIdUseCase.execute(id)
                )
        );
    }

    @Operation(summary = "Listar lotes de una finca con paginación")
    @ApiResponse(responseCode = "200", description = "Lista paginada de lotes")
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    @ApiResponse(responseCode = "404", description = "Finca no encontrada")
    @GetMapping("/{id}/lotes")
    public ResponseEntity<PageResponse<LoteResponse>> getLotesByFinca(
            @Parameter(description = "ID de la finca", example = "5") @PathVariable @Min(1) Long id,
            @Parameter(description = "Número de página (base 0)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Elementos por página (máx 100)", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(
                loteResponseMapper.toPageResponse(
                        getLotesByFincaIdUseCase.execute(id, page, size)
                )
        );
    }

    @Operation(summary = "Eliminar una finca")
    @ApiResponse(responseCode = "204", description = "Finca eliminada")
    @ApiResponse(responseCode = "404", description = "Finca no encontrada")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la finca", example = "5") @PathVariable @Min(1) Long id
    ) {
        deleteFincaByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

