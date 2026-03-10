package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.CreateUsuarioUseCase;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.web.dto.CreateUsuarioRequest;
import com.sioma.spotsapi.web.dto.UsuarioResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final CreateUsuarioUseCase useCase;

    public UsuarioController(CreateUsuarioUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public UsuarioResponse create(@RequestBody CreateUsuarioRequest request) {
        Usuario usuario = useCase.execute(
                request.nombre(),
                request.email(),
                request.password()
        );

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );
    }
}
