package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.mapper.LoteMapper;
import com.sioma.spotsapi.application.usecase.CreateLoteUseCase;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.infrastructure.geospatial.GeometryFactoryProvider;
import com.sioma.spotsapi.web.dto.CreateLoteRequest;
import com.sioma.spotsapi.web.dto.LoteResponse;
import org.locationtech.jts.geom.Polygon;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lotes")
public class LoteController {
    private final CreateLoteUseCase useCase;
    private final LoteMapper loteMapper;

    public LoteController(CreateLoteUseCase useCase, LoteMapper loteMapper) {
        this.useCase = useCase;
        this.loteMapper = loteMapper;
    }

    @PostMapping
    public LoteResponse create(@RequestBody CreateLoteRequest request){
        Polygon polygon = GeometryFactoryProvider
                .fromGeoJson(request.geocerca());

        Lote lote = useCase.execute(
                request.nombre(),
                polygon,
                request.fincaId(),
                request.tipoCultivoId()
        );

        return loteMapper.toResponse(lote);
    }
}
