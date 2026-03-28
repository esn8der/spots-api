package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePlantaRequest(@NotBlank String nombre) {
}