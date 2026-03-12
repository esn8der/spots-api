package com.sioma.spotsapi.domain.exception;

public class FincaNotExistsException extends RuntimeException {
    public FincaNotExistsException(Long id) {
        super("La Finca de id: " + id + " no existe");
    }
}
