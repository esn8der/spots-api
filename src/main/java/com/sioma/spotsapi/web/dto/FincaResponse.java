package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de una finca")
public record FincaResponse(
        @Schema(description = "ID único", example = "5") Long id,
        @Schema(description = "Nombre de la finca", example = "Finca Norte") String nombre
) {
}
