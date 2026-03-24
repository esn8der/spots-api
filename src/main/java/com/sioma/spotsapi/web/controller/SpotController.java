package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.CreateSpotUseCase;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.infrastructure.geospatial.GeometryFactoryProvider;
import com.sioma.spotsapi.web.dto.CreateSpotRequest;
import com.sioma.spotsapi.web.dto.SpotResponse;
import org.locationtech.jts.geom.Point;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spots")
public class SpotController {
    private final CreateSpotUseCase useCase;

    public SpotController(CreateSpotUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public SpotResponse create(@RequestBody CreateSpotRequest request) {
        Point point = GeometryFactoryProvider.fromGeoJsonPoint(request.coordenada());

        Spot spot = useCase.execute(
                point,
                request.loteId(),
                request.linea(),
                request.posicion()
        );

        return new SpotResponse(spot.getId(), spot.getLinea(), spot.getPosicion());
    }
}
