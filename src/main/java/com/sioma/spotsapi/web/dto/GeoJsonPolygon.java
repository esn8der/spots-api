package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Representación de la geocerca de un lote")
public record GeoJsonPolygon(
        @NotBlank @Schema(description = "Tipo de geometría", example = "Polygon")
        String type,
        @NotNull @Size(min = 4, message = "El polígono debe tener mínimo 4 puntos")
        @Schema(
                description = "Array de anillos lineales: [[lon,lat], ...]. El primer anillo define el perímetro exterior",
                example = "{\"type\":\"Polygon\",\"coordinates\":[[[-73.0,4.0],[-73.0,4.1],[-73.1,4.1],[-73.1,4.0],[-73.0,4.0]]]}"
        )
        List<List<List<Double>>> coordinates
) {
}