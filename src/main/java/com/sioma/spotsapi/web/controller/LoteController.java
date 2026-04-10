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
public class LoteController {
    private final CreateLoteUseCase useCase;
    private final GetLoteByIdUseCase getLoteByIdUseCase;
    private final DeleteLoteByIdUseCase deleteLoteByIdUseCase;
    private final UpdateLoteUseCase updateLoteUseCase;
    private final LoteResponseMapper loteResponseMapper;

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

    @GetMapping("/{id}")
    public ResponseEntity<LoteResponse> getById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(
                loteResponseMapper.toResponse(
                        getLoteByIdUseCase.execute(id)
                )
        );
    }

    @PatchMapping("/{id}/nombre")
    public ResponseEntity<LoteResponse> updateNombre(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdateLoteRequest request
    ) {
        Lote lote = updateLoteUseCase.execute(id, request.nombre());

        return ResponseEntity.ok(loteResponseMapper.toResponse(lote));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        deleteLoteByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
