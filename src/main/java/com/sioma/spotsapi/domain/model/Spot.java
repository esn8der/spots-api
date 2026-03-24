package com.sioma.spotsapi.domain.model;

import org.locationtech.jts.geom.Point;

public class Spot {
    private Long id;
    private final Point coordenada;
    private final Long loteId;
    private final int linea;
    private final int posicion;

    public Spot(Long id, Point coordenada, Long loteId, int linea, int posicion) {
        if (linea <= 0 || posicion <= 0) {
            throw new IllegalArgumentException("Linea y posición deben ser positivas");
        }
        this.id = id;
        this.coordenada = coordenada;
        this.loteId = loteId;
        this.linea = linea;
        this.posicion = posicion;
    }

    public Spot(Point coordenada, Long loteId, int linea, int posicion) {
        this(null, coordenada, loteId, linea, posicion);
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
    public int getLinea() {
        return linea;
    }
    public int getPosicion() {
        return posicion;
    }
}
