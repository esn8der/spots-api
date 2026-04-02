package com.sioma.spotsapi.domain.exception;

public class UsuarioNotFoundException extends RuntimeException {
    private final Long id;

    public UsuarioNotFoundException(Long id) {
        super("EL usuario de id: " + id + " no existe");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
