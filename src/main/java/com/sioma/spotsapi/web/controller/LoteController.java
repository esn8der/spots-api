package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.mapper.LoteMapper;
import com.sioma.spotsapi.application.usecase.CreateLoteUseCase;
import com.sioma.spotsapi.application.usecase.GetLoteByIdUseCase;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.infrastructure.geospatial.GeometryFactoryProvider;
import com.sioma.spotsapi.web.dto.CreateLoteRequest;
import com.sioma.spotsapi.web.dto.LoteResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Polygon;
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
    private final LoteMapper loteMapper;

    @PostMapping
    public ResponseEntity<LoteResponse> create(@Valid @RequestBody CreateLoteRequest request) {
        Polygon polygon = GeometryFactoryProvider
                .fromGeoJson(request.geocerca());

        Lote lote = useCase.execute(
                request.nombre(),
                polygon,
                request.fincaId(),
                request.tipoCultivoId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/lotes/" + lote.getId())
                .body(loteMapper.toResponse(lote));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteResponse> getById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(
                loteMapper.toResponse(
                        getLoteByIdUseCase.execute(id)
                )
        );
    }
}
