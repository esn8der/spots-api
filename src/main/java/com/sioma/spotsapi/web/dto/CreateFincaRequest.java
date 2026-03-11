package com.sioma.spotsapi.web.dto;

public record CreateFincaRequest(
        String nombre,
        Long usuarioId) {
}
