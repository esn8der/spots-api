package com.sioma.spotsapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para crear un nuevo usuario")
public record CreateUsuarioRequest(
        @NotBlank @Size(max = 50) String nombre,
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
