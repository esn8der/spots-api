package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSpotRequest(
        @NotNull GeoJsonPoint coordenada,
        @NotNull Long loteId,
        @NotNull @Positive Integer linea,
        @NotNull @Positive Integer posicion
) {
}