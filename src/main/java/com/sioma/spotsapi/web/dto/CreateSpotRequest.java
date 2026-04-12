package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Solicitud para crear un nuevo spot")
public record CreateSpotRequest(
        @NotNull GeoJsonPoint coordenada,
        @NotNull Long loteId,
        @NotNull @Positive Integer linea,
        @NotNull @Positive Integer posicion
) {
}