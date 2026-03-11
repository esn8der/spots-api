package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.CreateFincaUseCase;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.web.dto.CreateFincaRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fincas")
public class FincaController {
    private final CreateFincaUseCase useCase;

    public FincaController(CreateFincaUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public Finca create(@RequestBody CreateFincaRequest request) {
        return useCase.execute(request.nombre(), request.idUsuario());
    }
}
