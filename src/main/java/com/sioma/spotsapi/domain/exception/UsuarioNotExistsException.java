package com.sioma.spotsapi.domain.exception;

public class UsuarioNotExistsException extends RuntimeException {
    public UsuarioNotExistsException(Long id) {
        super("EL usuario de id: " + id + " no existe");
    }
}
