package com.sioma.spotsapi.domain.exception;

public class FincaAlreadyExistsException extends RuntimeException {
    public FincaAlreadyExistsException() {
        super("Esta finca ya existe");
    }
}
