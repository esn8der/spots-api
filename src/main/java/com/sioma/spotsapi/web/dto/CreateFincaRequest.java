package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFincaRequest(
        @NotBlank String nombre,
        @NotNull Long usuarioId) {
}
