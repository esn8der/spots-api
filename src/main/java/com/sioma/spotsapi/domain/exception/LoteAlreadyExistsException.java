package com.sioma.spotsapi.domain.exception;

public class LoteAlreadyExistsException extends RuntimeException {
    public LoteAlreadyExistsException() {
        super("El lote que intenta crear ya existe");
    }
}
