package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de un usuario")
public record UsuarioResponse(
        @Schema(description = "ID único", example = "5") Long id,
        @Schema(description = "Nombre del usuario", example = "Juan") String nombre,
        @Schema(description = "Correo del usuario", example = "juan@mail.com") String email
) {
}