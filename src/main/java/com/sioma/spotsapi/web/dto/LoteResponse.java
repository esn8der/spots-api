package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoteResponse(
        @NotNull Long id,
        @NotBlank String nombre
) {
}
