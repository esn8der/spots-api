package com.sioma.spotsapi.domain.model;

import org.locationtech.jts.geom.Point;

public class Spot {
    private Long id;
    private final Point coordenada;
    private final Long loteId;
    private final SpotPosition spotPosicion;

    public Spot(Long id, Point coordenada, Long loteId, SpotPosition spotPosicion) {
        this.id = id;
        this.coordenada = coordenada;
        this.loteId = loteId;
        this.spotPosicion = spotPosicion;
    }

    public Spot(Point coordenada, Long loteId, SpotPosition spotPosicion) {
        this(null, coordenada, loteId, spotPosicion);
    }

    public Long getId() {
        return id;
    }

    public Point getCoordenada() {
        return coordenada;
    }

    public Long getLoteId() {
        return loteId;
    }

    public SpotPosition getSpotPosicion() {
        return spotPosicion;
    }

    public int getLinea() {
        return spotPosicion.linea();
    }

    public int getPosicion() {
        return spotPosicion.posicion();
    }
}
