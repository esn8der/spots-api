package com.sioma.spotsapi.domain.model;

public class Finca {
    private Long id;
    private final String nombre;
    private final Long usuarioId;

    public Finca(Long id, String nombre, Long usuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.usuarioId = usuarioId;
    }

    public Finca(String nombre, Long usuarioId) {
        this.nombre = nombre;
        this.usuarioId = usuarioId;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}
