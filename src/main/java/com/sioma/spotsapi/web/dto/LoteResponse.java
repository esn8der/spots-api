package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de un lote")
public record LoteResponse(
        @Schema(description = "ID único", example = "5") Long id,
        @Schema(description = "Nombre del lote", example = "Lote sur") String nombre
) {
}
