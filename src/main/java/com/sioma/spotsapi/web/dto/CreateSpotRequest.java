package com.sioma.spotsapi.web.dto;

public record CreateSpotRequest(
        GeoJsonPoint coordenada,
        Long loteId,
        int linea,
        int posicion
) {}