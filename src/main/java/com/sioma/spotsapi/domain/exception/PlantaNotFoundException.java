package com.sioma.spotsapi.domain.exception;

public class PlantaNotFoundException extends RuntimeException {
    private final Long id;

    public PlantaNotFoundException(Long id) {
        super("La Planta de id : " + id + " no existe");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
