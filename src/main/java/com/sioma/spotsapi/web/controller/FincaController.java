package com.sioma.spotsapi.web.controller;

import org.springframework.web.bind.annotation.*;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.application.usecase.CreateFincaUseCase;
import com.sioma.spotsapi.application.usecase.GetLotesByFincaIdUseCase;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.web.dto.CreateFincaRequest;
import com.sioma.spotsapi.web.dto.LoteResponse;

import java.util.List;

@RestController
@RequestMapping("/fincas")
public class FincaController {
    private final CreateFincaUseCase useCase;
    private final GetLotesByFincaIdUseCase getLotesByFincaIdUseCase;

    public FincaController(CreateFincaUseCase useCase, GetLotesByFincaIdUseCase getLotesByFincaIdUseCase) {
        this.useCase = useCase;
        this.getLotesByFincaIdUseCase = getLotesByFincaIdUseCase;
    }

    @PostMapping
    public FincaResponse create(@RequestBody CreateFincaRequest request) {
        Finca finca = useCase.execute(request.nombre(), request.usuarioId());

        return new FincaResponse(finca.getId(), finca.getNombre());
    }

    @GetMapping("/{id}/lotes")
    public List<LoteResponse> getLotesByFinca(@PathVariable Long id) {

        return getLotesByFincaIdUseCase.execute(id)
                .stream()
                .map(l -> new LoteResponse(l.getId(), l.getNombre()))
                .toList();
    }
}
