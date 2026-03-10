package com.sioma.spotsapi.web.dto;

public record CreateUsuarioRequest(
        String nombre,
        String email,
        String password
) {
}
