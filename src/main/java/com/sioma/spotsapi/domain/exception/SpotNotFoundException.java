package com.sioma.spotsapi.domain.exception;

public class SpotNotFoundException extends RuntimeException {
    public SpotNotFoundException(Long id) {
        super("El spot de id: " + id + " no existe");
    }
}
