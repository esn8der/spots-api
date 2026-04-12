package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para crear un nuevo lote con geocerca GeoJSON")
public record CreateLoteRequest(
        @NotBlank @Size(max = 50) String nombre,
        @NotNull GeoJsonPolygon geocerca,
        @NotNull Long fincaId,
        @NotNull Long tipoCultivoId
) {
}
