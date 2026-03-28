package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotNull;

public record SpotResponse(
        @NotNull Long id,
        @NotNull int linea,
        @NotNull int posicion
) {
}
