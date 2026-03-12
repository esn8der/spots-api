package com.sioma.spotsapi.domain.exception;

public class PlantaNotExistsException extends RuntimeException {
    public PlantaNotExistsException(Long id) {
        super("La Planta de id : " + id + " no existe");
    }
}
