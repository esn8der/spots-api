package com.sioma.spotsapi.domain.exception;

public class PlantaNotFoundException extends RuntimeException {
    public PlantaNotFoundException(Long id) {
        super("La Planta de id : " + id + " no existe");
    }
}
