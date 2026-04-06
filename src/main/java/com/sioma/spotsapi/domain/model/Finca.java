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
        this(null, nombre, usuarioId);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Finca finca)) return false;
        return id != null && id.equals(finca.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
