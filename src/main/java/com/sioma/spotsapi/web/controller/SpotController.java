package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.web.mapper.SpotResponseMapper;
import com.sioma.spotsapi.application.usecase.CreateSpotUseCase;
import com.sioma.spotsapi.application.usecase.DeleteSpotByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetSpotByIdUseCase;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.infrastructure.geospatial.GeometryFactoryProvider;
import com.sioma.spotsapi.web.dto.CreateSpotRequest;
import com.sioma.spotsapi.web.dto.SpotResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/spots")
public class SpotController {
    private final CreateSpotUseCase useCase;
    private final GetSpotByIdUseCase getSpotByIdUseCase;
    private final DeleteSpotByIdUseCase deleteSpotByIdUseCase;
    private final SpotResponseMapper spotResponseMapper;

    @PostMapping
    public ResponseEntity<SpotResponse> create(@Valid @RequestBody CreateSpotRequest request) {
        Point point = GeometryFactoryProvider.fromGeoJsonPoint(request.coordenada());

        Spot spot = useCase.execute(
                point,
                request.loteId(),
                request.linea(),
                request.posicion()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/spots/" + spot.getId())
                .body(spotResponseMapper.toResponse(spot));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpotResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                spotResponseMapper.toResponse(
                        getSpotByIdUseCase.execute(id)
                )
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        deleteSpotByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
