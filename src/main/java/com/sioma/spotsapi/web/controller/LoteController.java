package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.CreateLoteUseCase;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.web.dto.CreateLoteRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lotes")
public class LoteController {
    private final CreateLoteUseCase useCase;

    public LoteController(CreateLoteUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public Lote create(@RequestBody CreateLoteRequest request){
        return useCase.execute(request.nombre(), request.fincaId(), request.tipoCultivoId());
    }
}
