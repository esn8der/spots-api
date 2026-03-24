package com.sioma.spotsapi.domain.exception;

public class LoteNotFoundException extends RuntimeException {
    public LoteNotFoundException(Long id) {
        super("El lote de id: " + id + " no existe");
    }
}
