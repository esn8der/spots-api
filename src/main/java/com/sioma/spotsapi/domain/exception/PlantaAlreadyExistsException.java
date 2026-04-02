package com.sioma.spotsapi.domain.exception;

public class PlantaAlreadyExistsException extends RuntimeException {

    private final String nombre;

    public PlantaAlreadyExistsException(String nombre) {
        super("La planta: " + nombre + " ya existe");
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}