package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUsuarioRequest(
        @NotBlank String nombre,
        @NotBlank String email,
        @NotBlank String password
) {
}
