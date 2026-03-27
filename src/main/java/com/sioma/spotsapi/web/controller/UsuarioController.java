package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.mapper.FincaMapper;
import com.sioma.spotsapi.application.mapper.UsuarioMapper;
import com.sioma.spotsapi.application.usecase.CreateUsuarioUseCase;
import com.sioma.spotsapi.application.usecase.GetFincasByUsuarioIdUseCase;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.web.dto.CreateUsuarioRequest;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.web.dto.UsuarioResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final CreateUsuarioUseCase useCase;
    private final GetFincasByUsuarioIdUseCase getFincasByUsuarioIdUseCase;
    private final UsuarioMapper usuarioMapper;
    private final FincaMapper fincaMapper;

    public UsuarioController(
            CreateUsuarioUseCase useCase,
            GetFincasByUsuarioIdUseCase getFincasByUsuarioIdUseCase,
            UsuarioMapper usuarioMapper,
            FincaMapper fincaMapper) {
        this.useCase = useCase;
        this.getFincasByUsuarioIdUseCase = getFincasByUsuarioIdUseCase;
        this.usuarioMapper = usuarioMapper;
        this.fincaMapper = fincaMapper;
    }

    @PostMapping
    public UsuarioResponse create(@RequestBody CreateUsuarioRequest request) {
        Usuario usuario = useCase.execute(
                request.nombre(),
                request.email(),
                request.password()
        );
        return usuarioMapper.toResponse(usuario);
    }

    @GetMapping("/{id}/fincas")
    public List<FincaResponse> getFincasByUsuario(@PathVariable Long id) {
        return fincaMapper.toResponseList(
                getFincasByUsuarioIdUseCase.execute(id)
        );
    }
}
