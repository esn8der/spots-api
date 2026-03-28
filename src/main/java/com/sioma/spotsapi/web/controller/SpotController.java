package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.mapper.SpotMapper;
import com.sioma.spotsapi.application.usecase.CreateSpotUseCase;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.infrastructure.geospatial.GeometryFactoryProvider;
import com.sioma.spotsapi.web.dto.CreateSpotRequest;
import com.sioma.spotsapi.web.dto.SpotResponse;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.Point;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spots")
public class SpotController {
    private final CreateSpotUseCase useCase;
    private final SpotMapper spotMapper;

    public SpotController(CreateSpotUseCase useCase, SpotMapper spotMapper) {
        this.useCase = useCase;
        this.spotMapper = spotMapper;
    }

    @PostMapping
    public SpotResponse create(@Valid @RequestBody CreateSpotRequest request) {
        Point point = GeometryFactoryProvider.fromGeoJsonPoint(request.coordenada());

        Spot spot = useCase.execute(
                point,
                request.loteId(),
                request.linea(),
                request.posicion()
        );

        return spotMapper.toResponse(spot);
    }
}
