package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.web.mapper.FincaResponseMapper;
import com.sioma.spotsapi.web.mapper.UsuarioResponseMapper;
import com.sioma.spotsapi.application.usecase.CreateUsuarioUseCase;
import com.sioma.spotsapi.application.usecase.DeleteUsuarioByIdUseCase;
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
    private final DeleteUsuarioByIdUseCase deleteUsuarioByIdUseCase;
    private final UsuarioResponseMapper usuarioResponseMapper;
    private final FincaResponseMapper fincaResponseMapper;

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
                .body(usuarioResponseMapper.toResponse(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(
                usuarioResponseMapper.toResponse(
                        getUsuarioByIdUseCase.execute(id)
                )
        );
    }

    @GetMapping("/{id}/fincas")
    public ResponseEntity<List<FincaResponse>> getFincasByUsuario(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(
                fincaResponseMapper.toResponseList(
                        getFincasByUsuarioIdUseCase.execute(id)
                )
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        deleteUsuarioByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

}
