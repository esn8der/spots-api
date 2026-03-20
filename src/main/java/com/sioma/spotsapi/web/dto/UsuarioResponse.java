package com.sioma.spotsapi.web.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email
) {}