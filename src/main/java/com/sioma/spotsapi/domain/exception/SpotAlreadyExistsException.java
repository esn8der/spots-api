package com.sioma.spotsapi.domain.exception;

public class SpotAlreadyExistsException extends RuntimeException {
    public SpotAlreadyExistsException() {
        super("Este spot ya existe en esta linea y posición");
    }
}
