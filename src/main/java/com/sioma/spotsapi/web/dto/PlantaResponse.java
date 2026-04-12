package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de una planta")
public record PlantaResponse(
        @Schema(description = "ID único", example = "5") Long id,
        @Schema(description = "Nombre de la planta", example = "Banano") String nombre
) {
}