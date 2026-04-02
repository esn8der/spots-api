package com.sioma.spotsapi.domain.exception;

public class LoteNotFoundException extends RuntimeException {
    private final Long id;

    public LoteNotFoundException(Long id) {
        super("El lote de id: " + id + " no existe");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
