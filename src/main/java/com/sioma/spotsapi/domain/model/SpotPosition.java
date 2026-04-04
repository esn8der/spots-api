package com.sioma.spotsapi.domain.model;

public record SpotPosition(int linea, int posicion) {
    public SpotPosition {
        if (linea <= 0 || posicion <= 0)
            throw new IllegalArgumentException(
                "Linea y posición deben ser positivas"
            );
    }
}