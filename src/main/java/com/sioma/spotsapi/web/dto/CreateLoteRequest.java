package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLoteRequest(
        @NotBlank @Size(max = 50) String nombre,
        @NotNull GeoJsonPolygon geocerca,
        @NotNull Long fincaId,
        @NotNull Long tipoCultivoId
) {
}
