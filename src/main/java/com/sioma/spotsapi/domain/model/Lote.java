package com.sioma.spotsapi.domain.model;

public class Lote {

    private Long id;
    private final String nombre;
    private final Long fincaId;
    private final Long tipoCultivoId;
    private final boolean onAgp;

    public Lote(Long id, String nombre, Long fincaId, Long tipoCultivoId) {
        this.id = id;
        this.nombre = nombre;
        this.fincaId = fincaId;
        this.tipoCultivoId = tipoCultivoId;
        this.onAgp = false;
    }

    public Lote(String nombre, Long fincaId, Long tipoCultivoId) {
        this.nombre = nombre;
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

    public Long getFincaId() {
        return fincaId;
    }

    public Long getTipoCultivoId() {
        return tipoCultivoId;
    }

    public boolean isOnAgp() {
        return onAgp;
    }
}