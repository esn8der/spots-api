package com.sioma.spotsapi.web.controller;

import org.springframework.web.bind.annotation.*;
import com.sioma.spotsapi.application.usecase.CreateUsuarioUseCase;
import com.sioma.spotsapi.application.usecase.GetFincasByUsuarioIdUseCase;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.web.dto.CreateUsuarioRequest;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.web.dto.UsuarioResponse;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final CreateUsuarioUseCase useCase;
    private final GetFincasByUsuarioIdUseCase getFincasByUsuarioIdUseCase;

    public UsuarioController(CreateUsuarioUseCase useCase, GetFincasByUsuarioIdUseCase getFincasByUsuarioIdUseCase) {
        this.useCase = useCase;
        this.getFincasByUsuarioIdUseCase = getFincasByUsuarioIdUseCase;
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

    @GetMapping("/{id}/fincas")
    public List<FincaResponse> getFincasByUsuario(@PathVariable Long id) {

        return getFincasByUsuarioIdUseCase.execute(id)
                .stream()
                .map(f -> new FincaResponse(f.getId(), f.getNombre()))
                .toList();
    }
}
