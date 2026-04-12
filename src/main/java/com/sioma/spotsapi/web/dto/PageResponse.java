package com.sioma.spotsapi.web.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Respuesta paginada genérica")
public record PageResponse<T>(
        @Schema(description = "Contenido de la página") List<T> content,
        @Schema(description = "Número de página actual (base 0)", example = "0") int page,
        @Schema(description = "Tamaño de página", example = "10") int size,
        @Schema(description = "Total de elementos", example = "42") long totalElements,
        @Schema(description = "Total de páginas", example = "5") int totalPages
) {
}