package com.sioma.spotsapi.domain.model;

public class Planta {

    private Long id;
    private String nombre;

    public Planta(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Planta(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}