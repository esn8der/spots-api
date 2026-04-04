package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GeoJsonPolygon(
        @NotBlank String type,
        @NotNull @Size(min = 4, message = "El polígono deben tener mínimo 4 puntos")
        List<List<List<Double>>> coordinates
) {
}