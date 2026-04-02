package com.sioma.spotsapi.domain.exception;

public class FincaNotFoundException extends RuntimeException {
    private final Long id;

    public FincaNotFoundException(Long id) {
        super("La Finca de id: " + id + " no existe");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
