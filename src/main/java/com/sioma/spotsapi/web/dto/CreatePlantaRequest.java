package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlantaRequest(@NotBlank @Size(max = 50) String nombre) {
}