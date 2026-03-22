package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLoteRequest(
        @NotBlank String nombre,
        @NotNull GeoJsonPolygon geocerca,
        @NotNull Long fincaId,
        @NotNull Long tipoCultivoId
) {}
