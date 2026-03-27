package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.mapper.FincaMapper;
import com.sioma.spotsapi.application.mapper.LoteMapper;
import com.sioma.spotsapi.application.usecase.CreateFincaUseCase;
import com.sioma.spotsapi.application.usecase.GetLotesByFincaIdUseCase;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.web.dto.CreateFincaRequest;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.web.dto.LoteResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fincas")
public class FincaController {
    private final CreateFincaUseCase useCase;
    private final GetLotesByFincaIdUseCase getLotesByFincaIdUseCase;
    private final FincaMapper fincaMapper;
    private final LoteMapper loteMapper;

    public FincaController(
            CreateFincaUseCase useCase,
            GetLotesByFincaIdUseCase getLotesByFincaIdUseCase,
            FincaMapper fincaMapper,
            LoteMapper loteMapper) {
        this.useCase = useCase;
        this.getLotesByFincaIdUseCase = getLotesByFincaIdUseCase;
        this.fincaMapper = fincaMapper;
        this.loteMapper = loteMapper;
    }

    @PostMapping
    public FincaResponse create(@RequestBody CreateFincaRequest request) {
        Finca finca = useCase.execute(request.nombre(), request.usuarioId());
        return fincaMapper.toResponse(finca);
    }

    @GetMapping("/{id}/lotes")
    public List<LoteResponse> getLotesByFinca(@PathVariable Long id) {
        return loteMapper.toResponseList(
                getLotesByFincaIdUseCase.execute(id)
        );
    }
}
