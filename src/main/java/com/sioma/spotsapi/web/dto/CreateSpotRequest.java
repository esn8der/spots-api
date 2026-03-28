package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotNull;

public record CreateSpotRequest(
        @NotNull GeoJsonPoint coordenada,
        @NotNull Long loteId,
        @NotNull int linea,
        @NotNull int posicion
) {
}