package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de un spot")
public record SpotResponse(
        @Schema(description = "ID único", example = "5") Long id,
        @Schema(description = "Línea a la que pertenece el spot", example = "1") int linea,
        @Schema(description = "Posición del spot en la linea a la que pertenece", example = "1") int posicion
) {
}
