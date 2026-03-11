package com.sioma.spotsapi.domain.model;

public class Finca {
    private Long id;
    private final String nombre;
    private Long idUsuario;

    public Finca(Long id, String nombre, Long idUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.idUsuario = idUsuario;
    }

    public Finca(String nombre, Long idUsuario) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }
}
