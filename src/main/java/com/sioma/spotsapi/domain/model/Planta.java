package com.sioma.spotsapi.domain.model;

public class Planta {

    private Long id;
    private final String nombre;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Planta planta)) return false;
        return id != null && id.equals(planta.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}