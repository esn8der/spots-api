package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.mapper.FincaMapper;
import com.sioma.spotsapi.application.mapper.UsuarioMapper;
import com.sioma.spotsapi.application.usecase.CreateUsuarioUseCase;
import com.sioma.spotsapi.application.usecase.GetFincasByUsuarioIdUseCase;
import com.sioma.spotsapi.application.usecase.GetUsuarioByIdUseCase;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.web.dto.CreateUsuarioRequest;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.web.dto.UsuarioResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {
    private final CreateUsuarioUseCase useCase;
    private final GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    private final GetFincasByUsuarioIdUseCase getFincasByUsuarioIdUseCase;
    private final UsuarioMapper usuarioMapper;
    private final FincaMapper fincaMapper;

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody CreateUsuarioRequest request) {
        Usuario usuario = useCase.execute(
                request.nombre(),
                request.email(),
                request.password()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/usuarios/" + usuario.getNombre())
                .body(usuarioMapper.toResponse(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(
                usuarioMapper.toResponse(
                        getUsuarioByIdUseCase.execute(id)
                )
        );
    }

    @GetMapping("/{id}/fincas")
    public ResponseEntity<List<FincaResponse>> getFincasByUsuario(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(
                fincaMapper.toResponseList(
                        getFincasByUsuarioIdUseCase.execute(id)
                )
        );
    }
}
