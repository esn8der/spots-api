package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlantaResponse(
        @NotNull Long id,
        @NotBlank String nombre
) {
}