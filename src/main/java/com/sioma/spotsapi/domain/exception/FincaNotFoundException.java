package com.sioma.spotsapi.domain.exception;

public class FincaNotFoundException extends RuntimeException {
    public FincaNotFoundException(Long id) {
        super("La Finca de id: " + id + " no existe");
    }
}
