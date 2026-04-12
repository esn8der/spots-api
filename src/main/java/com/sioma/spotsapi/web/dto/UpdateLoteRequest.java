package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para actualizar un lote")
public record UpdateLoteRequest(
        @NotBlank
        @Size(max = 50)
        String nombre
) {}