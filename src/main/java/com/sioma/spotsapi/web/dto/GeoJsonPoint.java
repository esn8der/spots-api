package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Representación de la coordenada de un spot")
public record GeoJsonPoint(
        @Schema(description = "Tipo de geometría", example = "Point")
        @NotBlank String type,
        @NotNull @Size(min = 2, max = 2, message = "Las coordenadas deben tener exactamente 2 elementos [longitud, latitud]")
        @Schema(description = "Coordenada del spot representado como array de dos elementos: [longitud, latitud]", example = "[-73.0,4.0]")
        List<Double> coordinates
) {
}