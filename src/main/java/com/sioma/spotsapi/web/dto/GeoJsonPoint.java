package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GeoJsonPoint(
        @NotBlank String type,
        @NotNull
        @Size(min = 2, max = 2, message = "Las coordenadas deben tener exactamente 2 elementos [longitud, latitud]")
        List<Double> coordinates
) {
}