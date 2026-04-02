package com.sioma.spotsapi.domain.exception;

public class SpotNotFoundException extends RuntimeException {
    private final Long id;

    public SpotNotFoundException(Long id) {
        super("El spot de id: " + id + " no existe");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
