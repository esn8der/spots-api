package com.sioma.spotsapi.domain.exception;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(Long id) {
        super("EL usuario de id: " + id + " no existe");
    }
}
