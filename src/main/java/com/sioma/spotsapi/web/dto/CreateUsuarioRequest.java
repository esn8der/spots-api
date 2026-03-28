package com.sioma.spotsapi.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUsuarioRequest(
        @NotBlank @Size(max = 50) String nombre,
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
