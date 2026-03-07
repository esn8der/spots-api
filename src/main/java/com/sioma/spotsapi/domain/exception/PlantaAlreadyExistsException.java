package com.sioma.spotsapi.domain.exception;

public class PlantaAlreadyExistsException extends RuntimeException {

    public PlantaAlreadyExistsException() {
        super("La planta ya existe");
    }
}