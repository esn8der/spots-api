package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioResponse(
        @NotNull Long id,
        @NotBlank String nombre,
        @NotBlank String email
) {
}