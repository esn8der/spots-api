package com.sioma.spotsapi.domain.model;

import com.sioma.spotsapi.domain.exception.PointOutsideLoteException;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class Lote {

    private Long id;
    private final String nombre;
    private final Polygon geocerca;
    private final Long fincaId;
    private final Long tipoCultivoId;
    private boolean onAgp;

    public Lote(Long id, String nombre, Polygon geocerca, Long fincaId, Long tipoCultivoId) {
        this.id = id;
        this.nombre = nombre;
        this.geocerca = geocerca;
        this.fincaId = fincaId;
        this.tipoCultivoId = tipoCultivoId;
        this.onAgp = false;
    }

    public Lote(String nombre, Polygon geocerca, Long fincaId, Long tipoCultivoId) {
        this.nombre = nombre;
        this.geocerca = geocerca;
        this.fincaId = fincaId;
        this.tipoCultivoId = tipoCultivoId;
        this.onAgp = false;
    }

    public Spot crearSpot(Point coordenada, SpotPosition posicion) {
        if (!this.geocerca.contains(coordenada))
            throw new PointOutsideLoteException();
        return new Spot(coordenada, this.id, posicion);
    }

    public void marcarComoEnAgp() {
        this.onAgp = true;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Polygon getGeocerca() {
        return geocerca;
    }

    public Long getFincaId() {
        return fincaId;
    }

    public Long getTipoCultivoId() {
        return tipoCultivoId;
    }

    @SuppressWarnings("unused")
    public boolean isOnAgp() {
        return onAgp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lote lote)) return false;
        return id != null && id.equals(lote.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}