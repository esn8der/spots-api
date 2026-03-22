package com.sioma.spotsapi.domain.model;

import org.locationtech.jts.geom.Geometry;

public class Lote {

    private Long id;
    private final String nombre;
    private final Geometry geocerca;
    private final Long fincaId;
    private final Long tipoCultivoId;
    private final boolean onAgp;

    public Lote(Long id, String nombre, Geometry geocerca, Long fincaId, Long tipoCultivoId) {
        this.id = id;
        this.nombre = nombre;
        this.geocerca = geocerca;
        this.fincaId = fincaId;
        this.tipoCultivoId = tipoCultivoId;
        this.onAgp = false;
    }

    public Lote(String nombre, Geometry geocerca, Long fincaId, Long tipoCultivoId) {
        this.nombre = nombre;
        this.geocerca = geocerca;
        this.fincaId = fincaId;
        this.tipoCultivoId = tipoCultivoId;
        this.onAgp = false;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Geometry getGeocerca() {
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