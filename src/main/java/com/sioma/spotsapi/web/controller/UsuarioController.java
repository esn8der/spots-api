package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.PaginationParams;
import com.sioma.spotsapi.web.dto.PageResponse;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios")
public class UsuarioController {
    private final CreateUsuarioUseCase useCase;
    private final GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    private final GetFincasByUsuarioIdUseCase getFincasByUsuarioIdUseCase;
    private final DeleteUsuarioByIdUseCase deleteUsuarioByIdUseCase;
    private final UsuarioResponseMapper usuarioResponseMapper;
    private final FincaResponseMapper fincaResponseMapper;

    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Payload inválido o datos faltantes")
    @ApiResponse(responseCode = "409", description = "Email de usuario duplicado")
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

    @Operation(summary = "Obtener usuario por ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no existe")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getById(
            @Parameter(description = "ID del usuario", example = "5") @PathVariable @Min(1) Long id
    ) {
        return ResponseEntity.ok(
                usuarioResponseMapper.toResponse(
                        getUsuarioByIdUseCase.execute(id)
                )
        );
    }

    @Operation(summary = "Listar fincas de un usuario con paginación")
    @ApiResponse(responseCode = "200", description = "Lista paginada de fincas")
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    @ApiResponse(responseCode = "404", description = "Usuario no existe")
    @GetMapping("/{id}/fincas")
    public ResponseEntity<PageResponse<FincaResponse>> getFincasByUsuario(
            @Parameter(description = "ID del usuario", example = "5") @PathVariable @Min(1) Long id,
            @Parameter(description = "Número de página (base 0)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Elementos por página (máx 100)", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Campo de ordenamiento", example = "nombre") @RequestParam(defaultValue = "nombre") String sortBy,
            @Parameter(description = "Dirección de ordenamiento", example = "asc") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PaginationParams params = PaginationParams.of(page, size, sortBy, sortDir);
        PageResult<Finca> fincaPage = getFincasByUsuarioIdUseCase.execute(id, params);

        return ResponseEntity.ok(fincaResponseMapper.toPageResponse(fincaPage));
    }

    @Operation(summary = "Eliminar un usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del usuario", example = "5") @PathVariable @Min(1) Long id
    ) {
        deleteUsuarioByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

}
