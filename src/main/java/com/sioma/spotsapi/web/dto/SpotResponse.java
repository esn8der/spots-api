package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Representación de un spot")
public record SpotResponse(
        @Schema(description = "ID único", example = "5") Long id,
        @Schema(description = "ID del lote asociado", example = "5") Long loteId,
        @Schema(description = "Línea a la que pertenece el spot", example = "1") int linea,
        @Schema(description = "Posición del spot en la linea a la que pertenece", example = "1") int posicion,
        @Schema(description = "Coordenada del spot en formato [longitud, latitud]", example = "[-34.603722, -58.381592]") List<Double> coordenada
) {
}
